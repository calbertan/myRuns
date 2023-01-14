package com.example.myRuns

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RunsRepository(private val databaseDao: RunsDatabaseDao) {
    val allRuns: Flow<List<Runs>> = databaseDao.getAllRuns()

    fun insert(runs:Runs){
        CoroutineScope(IO).launch{
            databaseDao.insertRuns(runs)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch{
            databaseDao.deleteRuns(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch{
            databaseDao.deleteAll()
        }
    }

}