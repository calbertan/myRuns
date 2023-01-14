package com.example.myRuns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Entry : AppCompatActivity() {
    private lateinit var database: RunsDatabase
    private lateinit var databaseDao: RunsDatabaseDao
    private lateinit var repository: RunsRepository
    private lateinit var viewModel: RunsViewModel
    private lateinit var factory: RunsViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        //gets values from extra
        val input = getInput(intent.getIntExtra("INPUT_KEY",0))
        this.findViewById<EditText>(R.id.input_entry).setText(input.toString())

        val activity = getActivity(intent.getIntExtra("ACTIVITY_KEY",0))
        this.findViewById<EditText>(R.id.activity_entry).setText(activity)

        val date = intent.getStringExtra("DATE_KEY")
        val time = intent.getStringExtra("TIME_KEY")
        this.findViewById<EditText>(R.id.date_time_entry).setText(time.plus(date))

        val duration = intent.getDoubleExtra("DURATION_KEY",0.0)
        var mins = duration.toInt()
        var secs = 60*(duration%1)
        val totalDuration=mins.toString().plus("mins ").plus(secs.toInt().toString()).plus("secs")
        this.findViewById<EditText>(R.id.duration_entry).setText(totalDuration.toString())

        val distance = intent.getDoubleExtra("DISTANCE_KEY",0.0)
        this.findViewById<EditText>(R.id.distance_entry).setText(distance.toString().plus(" miles"))

        val calories = intent.getDoubleExtra("CALORIES_KEY",0.0)
        this.findViewById<EditText>(R.id.calories_entry).setText(calories.toString().plus(" cals"))

        val heart = intent.getDoubleExtra("HEART_KEY",0.0)
        this.findViewById<EditText>(R.id.heart_entry).setText(heart.toString().plus(" bpm"))

        //sets buttons(title is too long, button on activity bar not ideal)
        val button1: Button = findViewById(R.id.entry_back)
        button1.setOnClickListener {
            finish()
        }
        val button2: Button = findViewById(R.id.entry_delete)
        button2.setOnClickListener {
            val id = intent.getLongExtra("ID_KEY",0L)
            val data = Intent()
            data.putExtra("DELETE_THIS",id)
            setResult(RESULT_OK, data)
            finish()
        }


    }

    fun getInput(typeInput: Int):String{
        if(typeInput==0)
            return "Manual"
        else if(typeInput==1)
            return "GPS"
        else if(typeInput==1)
            return "Automatic Entry"
        return ""
    }

    fun getActivity(typeActivity: Int): String {
        var activityType=""
        if(typeActivity == 0)
            activityType = activityType.plus("Running")
        else if(typeActivity == 1)
            activityType = activityType.plus("Walking")
        else if(typeActivity == 2)
            activityType = activityType.plus("Standing")
        else if(typeActivity == 3)
            activityType = activityType.plus("Cycling")
        else if(typeActivity == 4)
            activityType = activityType.plus("Hiking")
        else if(typeActivity == 5)
            activityType = activityType.plus("Downhill Skiing")
        else if(typeActivity == 6)
            activityType = activityType.plus("Cross-Country Skiing")
        else if(typeActivity == 7)
            activityType = activityType.plus("Snowboarding")
        else if(typeActivity == 8)
            activityType = activityType.plus("Skating")
        else if(typeActivity == 9)
            activityType = activityType.plus("Swimming")
        else if(typeActivity == 10)
            activityType = activityType.plus("Mountain Biking")
        else if(typeActivity == 11)
            activityType = activityType.plus("Wheelchair")
        else if(typeActivity == 12)
            activityType = activityType.plus("Eliptical")
        else if(typeActivity == 13)
            activityType = activityType.plus("Other")

        return activityType
    }
}