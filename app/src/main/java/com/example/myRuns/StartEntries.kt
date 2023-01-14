package com.example.myRuns

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import java.time.LocalDateTime
import java.util.*

class StartEntries : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val startMenu = arrayOf(
        "Date","Time","Duration","Distance","Calories","Heart Rate", "Comment"
    )

    companion object{
        const val ACTIVITY_KEY = "ACTIVITY"
        const val INPUT_KEY = "INPUT"
    }

    private val runId:Long = 0L
    private var runInput:Int = 0
    private var runActivity:Int = 0
    private var runDate:String = ""
    private var runTime:String = ""
    private var runDuration:Double = 0.0
    private var runDistance:Double = 0.0
    private val runPace:Double = 0.0
    private val runSpeed:Double = 0.0
    private var runCalorie:Double = 0.0
    private val runClimb:Double = 0.0
    private var runHeartRate:Double = 0.0
    private var runComment:String = ""
    private var runLatlng:String = ""
    val calendar = Calendar.getInstance()

    private val current = LocalDateTime.of(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH + 1),
        calendar.get(Calendar.DAY_OF_MONTH),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND)
    )

    private lateinit var myListView: ListView
    private lateinit var database: RunsDatabase
    private lateinit var databaseDao: RunsDatabaseDao
    private lateinit var repository: RunsRepository
    private lateinit var viewModel: RunsViewModel
    private lateinit var factory: RunsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(null)
        setContentView(R.layout.activity_start_layout)

        //initialize date and time
        runDate = dateToString(current.monthValue,current.dayOfMonth,current.year)
        runTime = runTime.plus(current.hour).plus(":").plus(current.minute).plus(":").plus(current.second)

        //initialize input type and activity type
        runInput = intent.getIntExtra("INPUT_KEY",0)
        runActivity = intent.getIntExtra("ACTIVITY_KEY",0)

        //listview of entries
        myListView = findViewById(R.id.myListView)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, startMenu)
        myListView.adapter = arrayAdapter
        myListView.setOnItemClickListener(){ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            if(position==0) {
                val date = DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

                date.show()
            }
            else if(position==1) {
                val time = TimePickerDialog(this, this, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),false)
                time.show()
            }
            else {
                buildDialog(position)
            }
        }

        //save entry
        val saveData:Button = findViewById(R.id.startSave)
        saveData.setOnClickListener {
            val newRun=Runs(
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
            println("debug: id=".plus(newRun.id))

            //starts thread to insert entry to database
            val thread = Thread(){
                database = RunsDatabase.getInstance(this)
                databaseDao = database.runsDatabaseDao
                repository = RunsRepository(databaseDao)

                factory = RunsViewModelFactory(repository)
                viewModel = ViewModelProvider(this, factory).get(RunsViewModel::class.java)
                viewModel.insert(newRun)

            }
            thread.start()
            finish()
        }

        //discard entry
        val cancelData:Button = findViewById(R.id.startCancel)
        cancelData.setOnClickListener {
            val toast = Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT)
            toast.show()

            finish()
        }

    }

    private fun buildDialog(position: Int) {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        var title:String = ""
        if(position==2)
            title="duration"
        else if(position==3)
            title="distance"
        else if(position==4)
            title="calories"
        else if(position==5)
            title="heart rate"
        else if(position==6)
            title="comment"
        builder.setTitle(title)

        // Set up the input
        val input = EditText(this)

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        if(position==6)
            input.inputType = InputType.TYPE_CLASS_TEXT
        else
            input.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        builder.setView(input)

        // Set up dialog buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            if(input.text.toString()=="")
            else if(position==2)
                runDuration=input.text.toString().toDouble()
            else if(position==3){
                //read unit preference
                val preference = PreferenceManager.getDefaultSharedPreferences(this)
                val unitDistance = preference.getString("unit","miles")

                //stores distance in miles
                if(unitDistance=="km") {
                    val temp = (input.text.toString().toDouble()) / 1.61
                    runDistance = String.format("%.2f", temp).toDouble()
                }
                else
                    runDistance=input.text.toString().toDouble()
            }
            else if(position==4)
                runCalorie=input.text.toString().toDouble()
            else if(position==5)
                runHeartRate=input.text.toString().toDouble()
            else if(position==6)
                runComment=input.text.toString()
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        runDate = ""
        runDate = dateToString(month,day,year)
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, min: Int) {
        runTime = ""
        runTime = runTime.plus(hour).plus(":").plus(min).plus(":").plus(current.second)
    }

    private fun dateToString(month: Int, dayOfMonth: Int, year: Int): String {
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
}

