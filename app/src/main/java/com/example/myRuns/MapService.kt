package com.example.myRuns

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import java.util.*
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instance
import weka.core.Instances
import java.util.concurrent.ArrayBlockingQueue


class MapService : Service(), LocationListener, SensorEventListener {
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "ChannelID"
    private val NOTIFICATION_ID = 1

    private lateinit var myBinder: Binder
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null
    private var duration = 0.0
    private lateinit var durationTask: TimerTask
    private lateinit var timer: Timer
    private var latlngstring = ""
    private lateinit var prevLoc: LatLng
    private var start = true

    private var sensorManager: SensorManager? = null
    private var x:Double = 0.0
    private var y:Double = 0.0
    private var z:Double = 0.0
    private var count = 0;
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private lateinit var mDataset: Instances
    private lateinit var mClassAttribute: Attribute

    companion object{
        val DURATION_KEY = "duration key"
        val DURATION_MSG = 1
        val LATLNG_KEY = "latlng key"
        val LATLNG_MSG = 2
        val DISTANCE_KEY = "distance key"
        val DISTANCE_MSG = 3
        val WEKA_KEY = "weka key"
        val WEKA_MSG = 4

    }

    override fun onCreate(){
        super.onCreate()
        println("debug: oncreate is called")

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()

        //execute durationTask every second
        initLocationManager()
        durationTask = DurationTask()
        timer = Timer()
        timer.scheduleAtFixedRate(durationTask, 0, 1000)

        myBinder = MyBinder()
    }

    override fun onBind(intent: Intent): IBinder?{
        println("debug: onbind is called")
        return myBinder
    }

    inner class MyBinder: Binder(){
        fun setMsgHandler(handler: Handler){
            msgHandler = handler
        }
    }

    override fun onUnbind(intent: Intent): Boolean{
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("debug: startcommand called: $startId")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1F, this)
        }

        val temp = intent?.getIntExtra("INPUT_TYPE",0)
        println("debug: activity type $temp")
        if(intent?.getIntExtra("INPUT_TYPE",0) == 2) {
            startSensorThread()
        }

        return START_NOT_STICKY
    }

    //starts the sensor service
    fun startSensorThread(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccBuffer = ArrayBlockingQueue<Double>(2048)
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        //starts thread for generating feature vector
        val thread = Thread {
            generateFeatureVector()
        }
        thread.start()
    }

    override fun onDestroy(){
        super.onDestroy()
        cleanupTask()
        stopForeground(true)
        if(sensorManager != null)
            sensorManager?.unregisterListener(this)
        if(locationManager != null)
            locationManager.removeUpdates(this)
    }

    private fun cleanupTask(){
        notificationManager.cancel(NOTIFICATION_ID)
        if(timer != null){
            timer.cancel()
        }
        duration = 0.0
    }

    private fun showNotification(){
        val intent = getPackageManager().getLaunchIntentForPackage("com.example.myRuns");
        val pendingIntent = PendingIntent.getActivity(this, 1, intent,
            PendingIntent.FLAG_MUTABLE)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, CHANNEL_ID)
        notificationBuilder.setContentTitle("myRuns")
        notificationBuilder.setContentText("Recording you path now")
        notificationBuilder.setSmallIcon(R.drawable.img)
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentIntent(pendingIntent)
        val notification = notificationBuilder.build()
        startForeground(NOTIFICATION_ID, notification)
        if(Build.VERSION.SDK_INT > 2.6){
            val channel = NotificationChannel(CHANNEL_ID, "Uncategorized",
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun initLocationManager(){
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)
            if(provider != null) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }
        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        latlngstring = latlngstring.plus(lat.toString()).plus(",").plus(lng.toString()).plus(":")

        println("debug: inside")

        //send latlng to viewmodel on location change
        val bundle1 = Bundle()
        bundle1.putString(LATLNG_KEY, latlngstring)
        val message = msgHandler?.obtainMessage()
        if(message != null){
            message.data = bundle1
            message.what = LATLNG_MSG
            msgHandler?.sendMessage(message)
        }

        //send distance to viewmodel on location change
        val message2 = msgHandler?.obtainMessage()
        if(message2 != null) {
            if (start) {    //on first run, prevLoc is set first
                prevLoc = LatLng(lat, lng)
                start = false
            } else {
                val distance = distanceBetween(prevLoc.latitude, prevLoc.longitude, lat, lng)
                val bundle2 = Bundle()
                println("debug: distance between is ${distance}")
                bundle2.putDouble(DISTANCE_KEY, distance)
                message2.data = bundle2
                message2.what = DISTANCE_MSG
                msgHandler?.sendMessage(message2)
            }
        }
    }

    //converts latlng to distance in miles
    private fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }
    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    //sends duration to viewmodel, incremented every second
    inner class DurationTask: TimerTask(){
        override fun run(){
            if(msgHandler != null){
                val bundle = Bundle()
                bundle.putDouble(DURATION_KEY, duration)
                val message = msgHandler?.obtainMessage()
                if(message != null){
                    message.data = bundle
                    message.what = DURATION_MSG
                    msgHandler?.sendMessage(message)
                }
            }
            duration++
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1] + (event.values[2]
                    * event.values[2])).toDouble())
            println("debug: sensor $m")
            count++


            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

   fun generateFeatureVector(){
       //generate feature vector
       count = 0
       var blockSize = 0;
       val inst: Instance = DenseInstance(64 + 2)
       val accBlock = DoubleArray(64)
       val fft = FFT(64)
       val im = DoubleArray(64)

       while(true){
           //dump buffer to accBlock
           accBlock[blockSize++] = mAccBuffer.take().toDouble()

           if(blockSize == 64){
               blockSize = 0
               //find max in accBlock
               var max = .0
               for (`val` in accBlock) {
                   if (max < `val`) {
                       max = `val`
                   }
               }

               fft.fft(accBlock, im)
               for (i in accBlock.indices) {
                   val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                           * im[i])
                   inst.setValue(i, mag)
                   im[i] = .0 // Clear the field
               }
               // Append max after frequency component
               inst.setValue(64, max)

               val activityType = WekaClassifier.classify(inst.toDoubleArray().toTypedArray())

               //sends activity type to viewmodel
               val bundle = Bundle()
               bundle.putDouble(WEKA_KEY, activityType)
               val message = msgHandler?.obtainMessage()
               if(message != null){
                   message.data = bundle
                   message.what = WEKA_MSG
                   msgHandler?.sendMessage(message)
               }
           }
       }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

}