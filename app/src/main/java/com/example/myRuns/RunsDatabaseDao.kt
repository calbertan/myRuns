package com.example.myRuns

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunsDatabaseDao {
    @Insert
    suspend fun insertRuns(runs: Runs)

    @Query("SELECT * FROM runs_table")
    fun getAllRuns() : Flow<List<Runs>>

    @Query("DELETE FROM runs_table WHERE id=:key")
    suspend fun deleteRuns(key:Long)

    @Query("DELETE FROM runs_table")
    suspend fun deleteAll()
}