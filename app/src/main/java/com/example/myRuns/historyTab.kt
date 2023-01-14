package com.example.myRuns

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class historyTab : Fragment() {

    private lateinit var listView: ListView
    private lateinit var myAdapter: MyAdapter
    private lateinit var arrayList: ArrayList<Runs>

    private lateinit var database: RunsDatabase
    private lateinit var databaseDao: RunsDatabaseDao
    private lateinit var repository: RunsRepository
    private lateinit var viewModel: RunsViewModel
    private lateinit var factory: RunsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_tab, container, false)
        listView = view.findViewById(R.id.entries)
        arrayList = ArrayList()
        myAdapter = MyAdapter(requireActivity(),arrayList)
        listView.adapter = myAdapter

        //initialize database
        database = RunsDatabase.getInstance(requireActivity())
        databaseDao = database.runsDatabaseDao
        repository = RunsRepository(databaseDao)

        factory = RunsViewModelFactory(repository)
        viewModel = ViewModelProvider(requireActivity(), factory).get(RunsViewModel::class.java)

        viewModel.allRunsLiveData.observe(requireActivity()){
            //update listView
            myAdapter.replaceList(it)
            myAdapter.notifyDataSetChanged()
        }

        //when item in list is clicked, starts the Entry activity
        listView.setOnItemClickListener(){ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val entry = listView.getItemAtPosition(position) as Runs
            if(entry.inputType == 0) {
                val intent = Intent(requireActivity(), Entry::class.java)
                println("debug: id is ${entry.id}")
                intent.putExtra("ID_KEY", entry.id)
                intent.putExtra("INPUT_KEY", entry.inputType)
                intent.putExtra("ACTIVITY_KEY", entry.activityType)
                intent.putExtra("DATE_KEY", entry.date)
                intent.putExtra("TIME_KEY", entry.time)
                intent.putExtra("DISTANCE_KEY", entry.distance)
                intent.putExtra("DURATION_KEY", entry.duration)
                intent.putExtra("CALORIES_KEY", entry.calorie)
                intent.putExtra("HEART_KEY", entry.heartRate)

                //starts activity for result
                startActivityForResult(intent, 1)
            }
            else{
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                println("debug: id is ${entry.id}")
                intent.putExtra("ID_KEY", entry.id)
                intent.putExtra("INPUT_KEY", entry.inputType)
                intent.putExtra("ACTIVITY_KEY", entry.activityType)
                intent.putExtra("LOCATION_KEY",entry.locationList)
                intent.putExtra("PACE_KEY", entry.avgPace)
                intent.putExtra("SPEED_KEY", entry.avgSpeed)
                intent.putExtra("CLIMB_KEY", entry.climb)
                intent.putExtra("CALORIES_KEY", entry.calorie)
                intent.putExtra("DISTANCE_KEY", entry.distance)
                startActivityForResult(intent, 1)
            }

        }


        return view
    }

    //gets result from activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK){
            val delete = data?.getLongExtra("DELETE_THIS",0L)
            val thread = Thread {
                if (delete != null) {
                    viewModel.deleteRuns(delete)
                }
            }
            thread.start()
        }
    }


}