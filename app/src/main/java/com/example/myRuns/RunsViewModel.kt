package com.example.myRuns

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.*
import java.lang.IllegalArgumentException


class RunsViewModel(private val repository: RunsRepository): ViewModel(), ServiceConnection {

    //handles database
    val allRunsLiveData = repository.allRuns.asLiveData()

    fun insert(runs: Runs){
        repository.insert(runs)
    }

    fun deleteRuns(id:Long){
        val runsList= allRunsLiveData.value
        if(runsList != null && runsList.size > 0){
            println("debug: delete")
            for(i in 0..runsList.size-1) {
                if (id == runsList[i].id) {
                    println("debug: delete")
                    repository.delete(id)
                }
            }
        }
    }

    fun deleteAll(){
        val runsList= allRunsLiveData.value
        if(runsList != null && runsList.size > 0){
            repository.deleteAll()
        }
    }

    //handles service
    private val _locationLiveData = MutableLiveData<Runs>()
    private val myMsgHandler = MyMsgHandler(Looper.getMainLooper())
    private val runId:Long = 0L
    private var runInput:Int = 0
    private var runActivity:Int = 14
    private var runDate:String = ""
    private var runTime:String = ""
    private var runDuration:Double = 0.0
    private var runDistance:Double = 0.0
    private var runPace:Double = 0.0
    private var runSpeed:Double = 0.0
    private var runCalorie:Double = 0.0
    private var runClimb:Double = 0.0
    private var runHeartRate:Double = 0.0
    private var runComment:String = ""
    private var runLatlng: String = ""

    private var currentDist: Double = 0.0
    private var currentDur: Double = 0.01

    val serviceCounter: LiveData<Runs>
        get(){
            return _locationLiveData
        }

    override fun onServiceConnected(name: ComponentName, iBinder: IBinder){
        println("debug: onserviceconnected")
        val binder = iBinder as MapService.MyBinder
        binder.setMsgHandler(myMsgHandler)
    }

    override fun onServiceDisconnected(name: ComponentName){
        println("debug: onservicedisconnected")
    }

    inner class MyMsgHandler(looper: Looper): Handler(looper){
        override fun handleMessage(msg: Message) {
            val newRun = Runs(
                inputType=runInput,
                activityType = runActivity,
                date = runDate,
                time = runTime,
                duration = runDuration,
                distance = runDistance,
                avgPace = runPace,
                avgSpeed = runSpeed,
                calorie = runCalorie,
                climb = runClimb,
                heartRate = runHeartRate,
                comment = runComment,
                locationList = runLatlng
            )

            //updates the data and sends it to map
            //updates every second
            if(msg.what == MapService.DURATION_MSG){
                val bundle = msg.data
                runDuration = bundle.getDouble(MapService.DURATION_KEY)
                runPace = (runDistance / runDuration) * 60
                runSpeed = (currentDist / currentDur) * 60

                if(runDuration % 10.0 == 0.0) {
                    runCalorie += 1.0 + runPace
                }

                newRun.duration = runDuration
                _locationLiveData.value = newRun
            }
            //latlng updates on location change
            if(msg.what == MapService.LATLNG_MSG){
                println("debug: location in service")
                val bundle = msg.data
                val newLatlng = bundle.getString(MapService.LATLNG_KEY)
                if (newLatlng != null) {
                    runLatlng = newLatlng
                    newRun.locationList = runLatlng
                    _locationLiveData.value = newRun
                }
            }
            //distance updates on location change
            if(msg.what == MapService.DISTANCE_MSG){
                val bundle = msg.data
                val newDistance = bundle.getDouble(MapService.DISTANCE_KEY)
                if (newDistance != null) {
                    runDistance += newDistance
                    currentDist = runDistance - currentDist
                    currentDur = runDuration - currentDur
//                    println("debug: currentdist = ${currentDist}")
//                    println("debug: currentdur = ${currentDur}")
                    newRun.distance = runDistance
                    _locationLiveData.value = newRun
                }
            }
            //activity type
            if(msg.what == MapService.WEKA_MSG){
                val bundle = msg.data
                val newActivity = bundle.getDouble(MapService.WEKA_KEY)
                if (newActivity != null) {
                    runActivity = newActivity.toInt()
                    newRun.activityType = runActivity
                    _locationLiveData.value = newRun
                }
            }
        }
    }
}

class RunsViewModelFactory(private val repository: RunsRepository)
    : ViewModelProvider.Factory{
        override fun <T:ViewModel> create(modelClass:Class<T>): T{
            if (modelClass.isAssignableFrom(RunsViewModel::class.java))
                return RunsViewModel(repository) as T
            throw IllegalArgumentException("Error")
        }
    }



