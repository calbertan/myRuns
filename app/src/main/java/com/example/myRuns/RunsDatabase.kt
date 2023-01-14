package com.example.myRuns

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Runs::class],version = 1)
abstract class RunsDatabase : RoomDatabase() {
    abstract val runsDatabaseDao: RunsDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: RunsDatabase? = null

        fun getInstance(context:Context):RunsDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RunsDatabase::class.java,
                        "runs_DB").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}