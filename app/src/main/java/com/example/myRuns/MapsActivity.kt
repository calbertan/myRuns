package com.example.myRuns

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.myRuns.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.time.LocalDateTime
import java.util.*
import kotlin.math.round

class MapsActivity : AppCompatActivity(), OnMapReadyCallback{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var markerOptions: MarkerOptions
    private var mapCentered = false
    private lateinit var polylineOptions:PolylineOptions
    private lateinit var prevMarker: Marker
    private var prevPolyline: Polyline? = null
    private var isBind = false
    private var currentDist = 0.0;
    private var isEntry = false
    private lateinit var latlngEntry: Array<LatLng>
    private var initialized = false;

    private lateinit var newRun: Runs
    private var runInput:Int = 0
    private var runActivity:Int = 0
    private var runDate:String = ""
    private var runTime:String = ""

    val calendar = Calendar.getInstance()

    private val current = LocalDateTime.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH + 1),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )

    private lateinit var database: RunsDatabase
    private lateinit var databaseDao: RunsDatabaseDao
    private lateinit var repository: RunsRepository
    private lateinit var viewModel: RunsViewModel
    private lateinit var factory: RunsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //default newRun
        newRun = Runs(
            inputType=0,
            activityType = 0,
            date = "",
            time = "",
            duration = 0.0,
            distance = 0.0,
            avgPace = 0.0,
            avgSpeed = 0.0,
            calorie = 0.0,
            climb = 0.0,
            heartRate = 0.0,
            comment = "",
            locationList = ""
        )

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val input = intent.getStringExtra("LOCATION_KEY")

        //if activity is started and entry exists in database
        if(input != null){
            isEntry = true;
            if(input != "") {
                latlngEntry = stringToLatLng(input)
                initialized = true
            }
            setEntryButtons()

            //get all the textview of the map overlay
            val mapType = findViewById<TextView>(R.id.map_type)
            //initialize input type, activity type
            runInput = intent.getIntExtra("INPUT_KEY", 0)
            runActivity = intent.getIntExtra("ACTIVITY_KEY", 0)
            mapType.text = "Type: ".plus(getActivity(runActivity))


            updateOverlay(
                intent.getDoubleExtra("PACE_KEY",0.0),
                intent.getDoubleExtra("SPEED_KEY",0.0),
                intent.getDoubleExtra("CLIMB_KEY",0.0),
                intent.getDoubleExtra("CALORIES_KEY",0.0),
                intent.getDoubleExtra("DISTANCE_KEY",0.0))

        }else {
            isEntry = false
            //get all the textview of the map overlay
            val mapType = findViewById<TextView>(R.id.map_type)

            //initialize date and time
            runDate = dateToString(current.monthValue, current.dayOfMonth, current.year)
            runTime = runTime.plus(current.hour).plus(":").plus(current.minute).plus(":")
                .plus(current.second)

            //initialize input type, activity type
            runInput = intent.getIntExtra("INPUT_KEY", 0)
            runActivity = intent.getIntExtra("ACTIVITY_KEY", 0)
            if (runInput == 1)
                mapType.text = "Type: ".plus(getActivity(runActivity))
            else {
                runActivity = 14
                mapType.text = "Type: Unknown"
            }

            //setup the viewmodel for service and database
            database = RunsDatabase.getInstance(applicationContext)
            databaseDao = database.runsDatabaseDao
            repository = RunsRepository(databaseDao)

            factory = RunsViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory).get(RunsViewModel::class.java)

            viewModel.serviceCounter.observe(this) {
                newRun = it
                currentDist = newRun.distance - currentDist

                //update location
                if (it.locationList != "") {
                    println("debug: ok")
                    val latlngArray = stringToLatLng(newRun.locationList)
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngArray[0], 17f)
                    if (!mapCentered) {
                        //on first time opening map
                        mMap.animateCamera(cameraUpdate)
                        markerOptions.position(latlngArray[0])
                        markerOptions.icon(BitmapDescriptorFactory.
                            defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        mMap.addMarker(markerOptions)
                        prevMarker = mMap.addMarker(markerOptions)
                        mapCentered = true;
                    } else {
                        //everytime you move, remove last marker and add marker at newest location
                        prevMarker.remove()
                        markerOptions.position(latlngArray[latlngArray.size - 1])
                        markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        prevMarker = mMap.addMarker(markerOptions)

                        //center the marker
                        var contains = mMap.getProjection()
                            .getVisibleRegion()
                            .latLngBounds
                            .contains(latlngArray[latlngArray.size - 1])
                        if (!contains){
                            val cameraUpdate =
                                CameraUpdateFactory.newLatLngZoom(latlngArray[latlngArray.size - 1], 17f)
                            mMap.animateCamera(cameraUpdate)
                        }

                        if(prevPolyline!=null)
                            prevPolyline?.remove()
                        prevPolyline = mMap.addPolyline(PolylineOptions().addAll(latlngArray.toMutableList()))
                    }
                }

                //updates activity type for automatic
                if(runActivity != newRun.activityType){
                    runActivity = newRun.activityType
                    var activity = ""
                    if(runActivity == 0) {
                        activity = "standing"
                        runActivity = 2 //change it to match dropdown menu
                    }
                    else if(runActivity == 1){
                        activity = "walking"
                    }
                    else if(runActivity == 2) {
                        activity = "running"
                        runActivity = 0 //change it to match dropdown menu
                    }

                    val mapType = findViewById<TextView>(R.id.map_type)
                    mapType.text = "Type: ".plus(activity)

                }

                //update overlays
                updateOverlay(newRun.avgPace,newRun.avgSpeed,newRun.climb,newRun.calorie,newRun.distance)
            }

            //enter entry into database
            val saveData: Button = findViewById(R.id.mapSave)
            saveData.setOnClickListener {

                //starts thread to insert entry to database
                val thread = Thread() {
                    newRun.inputType = runInput
                    newRun.activityType = runActivity
                    newRun.date = runDate
                    newRun.time = runTime
                    newRun.duration = newRun.duration / 60
                    println("debug: duration is ${newRun.duration}")

                    viewModel.insert(newRun)
                }
                thread.start()

                val intent = Intent(this, MapService::class.java)
                stopService(intent)

                finish()
            }

            //discard entry button
            val cancelData: Button = findViewById(R.id.mapCancel)
            cancelData.setOnClickListener {
                val toast = Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT)
                toast.show()

                val intent = Intent(this, MapService::class.java)
                stopService(intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        markerOptions = MarkerOptions()

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        //checks permission if this is a new entry
        if(!isEntry)
            checkPermission()
        else if(initialized){
            //add polylines and markers to map if this is an entry from the database
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlngEntry[latlngEntry.size-1], 17f)
            mMap.animateCamera(cameraUpdate)

            markerOptions.position(latlngEntry[0])
            markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            mMap.addMarker(markerOptions)

            markerOptions.position(latlngEntry[latlngEntry.size - 1])
            markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMap.addMarker(markerOptions)

            mMap.addPolyline(PolylineOptions().addAll(latlngEntry.toMutableList()))
        }

    }

    override fun onDestroy(){
        super.onDestroy()
        val intent = Intent(this, MapService::class.java)
        applicationContext.stopService(intent)
        unbindService()

    }

    //checks location permission
    fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }else {
            val intent = Intent(this, MapService::class.java)
            intent.putExtra("INPUT_TYPE", runInput)
            applicationContext.startForegroundService(intent)
            bindService(intent)
        }
    }

    //updates the text that is overlayed on top of the map
    fun updateOverlay(avgPace:Double,avgSpeed:Double,climb:Double,calorie:Double,distance:Double){
        val mapAvg = findViewById<TextView>(R.id.map_avg)
        val mapCur = findViewById<TextView>(R.id.map_cur)
        val mapClimb = findViewById<TextView>(R.id.map_climb)
        val mapCal = findViewById<TextView>(R.id.map_calorie)
        val mapDist = findViewById<TextView>(R.id.map_distance)
        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val unitDistance = preference.getString("unit", "miles")
        if (unitDistance == "km") {
            mapAvg.text =
                "Avg speed: ".plus(round(avgPace * 160.9) / 100).plus(" km/h")
            mapCur.text =
                "Cur speed: ".plus(round(avgSpeed * 160.9) / 100).plus(" km/h")
            mapClimb.text =
                "Climb: ".plus(round(climb * 160.9) / 100).plus(" kilometers")
            mapCal.text = "Calorie: ".plus(round(calorie * 100) / 100)
            mapDist.text =
                "Distance: ".plus(round(distance * 160.9) / 100).plus(" km")
        } else {
            mapAvg.text = "Avg speed: ".plus(round(avgPace * 100) / 100).plus(" m/h")
            mapCur.text =
                "Cur speed: ".plus(round(avgSpeed * 100) / 100).plus(" m/h")
            mapClimb.text = "Climb: ".plus(round(climb * 100) / 100).plus(" miles")
            mapCal.text = "Calorie: ".plus(round(calorie * 100) / 100)
            mapDist.text =
                "Distance: ".plus(round(distance * 100) / 100).plus(" miles")
        }
    }

    //creates a delete button if this is an entry that exists in database
    fun setEntryButtons(){
        val deleteData: Button = findViewById(R.id.mapSave)
        deleteData.setText("DELETE")
        deleteData.setOnClickListener {

            val id = intent.getLongExtra("ID_KEY",0L)
            val data = Intent()
            data.putExtra("DELETE_THIS",id)
            setResult(RESULT_OK, data)

            finish()
        }

        //discard the entry
        val cancelData: Button = findViewById(R.id.mapCancel)
        cancelData.setOnClickListener {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(this, MapService::class.java)
                intent.putExtra("INPUT_TYPE", runInput)
                applicationContext.startForegroundService(intent)
                bindService(intent)
            }
        }
    }

    private fun bindService(intent:Intent){
        if(!isBind){
            this.applicationContext.bindService(intent,viewModel, Context.BIND_AUTO_CREATE)
            isBind=true
        }
    }

    private fun unbindService(){
        if(isBind){
            this.applicationContext.unbindService(viewModel)
            isBind=false
        }
    }

    //converts string into array of latlng
    fun stringToLatLng(locations: String): Array<LatLng>{
        //locations = 49.2559755,-122.8993428:49.2559701,-122.8993296:
        println("debug: ".plus(locations))
        var string = locations
        var newlatlng: String
        var lat:Double
        var lng:Double
        var latlngArray: Array<LatLng> = arrayOf()

        var colon = string.indexOf(":")
        var comma = string.indexOf(",")

        //insert initial location to array
        newlatlng = string.substring(0,colon)
        string = string.substring(colon+1,string.length)
        lat = newlatlng.substring(0,comma).toDouble()
        lng = newlatlng.substring(comma+1,newlatlng.length).toDouble()
        latlngArray = latlngArray.plus(LatLng(lat,lng))

        //while string is not just ":", convert string to a latlng
        while(string.length > 2){
            colon = string.indexOf(":")
            comma = string.indexOf(",")
            newlatlng = string.substring(0,colon)
            string = string.substring(colon+1,string.length)
            lat = newlatlng.substring(0,comma).toDouble()
            lng = newlatlng.substring(comma+1,newlatlng.length).toDouble()
            latlngArray = latlngArray.plus(LatLng(lat,lng))
        }
        return latlngArray
    }

    fun dateToString(month: Int, dayOfMonth: Int, year: Int): String {
        var date:String=""
        if(month==1)
            date = " Jan "
        if(month==2)
            date = " Feb "
        if(month==3)
            date = " Mar "
        if(month==4)
            date = " Apr "
        if(month==5)
            date = " May "
        if(month==6)
            date = " Jun "
        if(month==7)
            date = " Jul "
        if(month==8)
            date = " Aug "
        if(month==9)
            date = " Sep "
        if(month==10)
            date = " Oct "
        if(month==11)
            date = " Nov "
        if(month==12)
            date = " Dec "

        date = date.plus(dayOfMonth).plus(" ").plus(year)

        return date
    }

    fun getActivity(typeActivity: Int): String {
        var newActivity=""
        if(typeActivity == 0)
            newActivity = newActivity.plus("Running")
        else if(typeActivity == 1)
            newActivity = newActivity.plus("Walking")
        else if(typeActivity == 2)
            newActivity = newActivity.plus("Standing")
        else if(typeActivity == 3)
            newActivity = newActivity.plus("Cycling")
        else if(typeActivity == 4)
            newActivity = newActivity.plus("Hiking")
        else if(typeActivity == 5)
            newActivity = newActivity.plus("Downhill Skiing")
        else if(typeActivity == 6)
            newActivity = newActivity.plus("Cross-Country Skiing")
        else if(typeActivity == 7)
            newActivity = newActivity.plus("Snowboarding")
        else if(typeActivity == 8)
            newActivity = newActivity.plus("Skating")
        else if(typeActivity == 9)
            newActivity = newActivity.plus("Swimming")
        else if(typeActivity == 10)
            newActivity = newActivity.plus("Mountain Biking")
        else if(typeActivity == 11)
            newActivity = newActivity.plus("Wheelchair")
        else if(typeActivity == 12)
            newActivity = newActivity.plus("Eliptical")
        else if(typeActivity == 13)
            newActivity = newActivity.plus("Other")
        else if(typeActivity == 14)
            newActivity = newActivity.plus("Unknown")

        return newActivity
    }
}