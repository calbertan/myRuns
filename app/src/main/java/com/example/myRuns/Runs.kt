package com.example.myRuns


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs_table")
data class Runs(
    @PrimaryKey(autoGenerate = true) var id: Long=0,
    @ColumnInfo(name = "inputType") var inputType: Int = 0,  // Manual, GPS or automatic
    @ColumnInfo(name = "activityType") var activityType: Int = 0,   // Running, cycling etc.
    @ColumnInfo(name = "time")var date: String = "",   // When does this entry happen
    @ColumnInfo(name = "date")var time: String = "",
    @ColumnInfo(name = "duration") var duration: Double = 0.0,      // Exercise duration in minutes
    @ColumnInfo(name = "distance") var distance: Double = 0.0,  // Distance traveled in miles
    @ColumnInfo(name = "avgPace") var avgPace: Double = 0.0,      // Average pace
    @ColumnInfo(name = "avgSpeed") var avgSpeed: Double = 0.0,     // Average speed
    @ColumnInfo(name = "calorie") var calorie: Double = 0.0,        // Calories burnt
    @ColumnInfo(name = "climb") var climb: Double = 0.0,       // Climb. Either in meters or feet.
    @ColumnInfo(name = "heartRate") var heartRate: Double = 0.0,       // Heart rate
    @ColumnInfo(name = "comment") var comment: String = "",   // Comments
    @ColumnInfo(name = "latlng") var locationList: String = ""  // Location list
        )


