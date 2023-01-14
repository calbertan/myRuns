package com.example.myRuns

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.preference.PreferenceManager


class MyAdapter(private val context: Context, private var list: List<Runs>): BaseAdapter() {
    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val view = View.inflate(context, R.layout.adapter_layout, null)
        val textViewId = view.findViewById<TextView>(R.id.entry_date)
        val textViewString = view.findViewById<TextView>(R.id.entry_dist)

        var activityType = ""

        activityType = activityType.plus(getInput(list[position].inputType))
            .plus(getActivity(list[position].activityType))
            .plus(list[position].time)
            .plus(list[position].date)

        textViewId.text=activityType


        //read unit preference
        val preference = PreferenceManager.getDefaultSharedPreferences(context)
        val unitDistance = preference.getString("unit","miles")

        var entryDist:String

        //converts to km if selected
        if(unitDistance=="km") {
            val temp = (1.609 * list[position].distance)
            entryDist = String.format("%.2f", temp).plus(" km, ")
        }
        else{
            val temp = list[position].distance
            entryDist = String.format("%.2f", temp).plus(" Miles, ")
        }

        //converts duration to mins and secs
        var mins = list[position].duration.toInt()
        var secs = 60*(list[position].duration%1)
        entryDist = entryDist.plus(mins.toString()).plus("mins ").plus(secs.toInt().toString()).plus("secs")
        textViewString.text=entryDist

        return view
    }

    fun replaceList(newList:List<Runs>){
            list = newList
    }

    fun getActivity(typeActivity: Int): String {
        var activityType=""
        if(typeActivity == 0)
            activityType = activityType.plus("Running, ")
        else if(typeActivity == 1)
            activityType = activityType.plus("Walking, ")
        else if(typeActivity == 2)
            activityType = activityType.plus("Standing, ")
        else if(typeActivity == 3)
            activityType = activityType.plus("Cycling, ")
        else if(typeActivity == 4)
            activityType = activityType.plus("Hiking, ")
        else if(typeActivity == 5)
            activityType = activityType.plus("Downhill Skiing, ")
        else if(typeActivity == 6)
            activityType = activityType.plus("Cross-Country Skiing, ")
        else if(typeActivity == 7)
            activityType = activityType.plus("Snowboarding, ")
        else if(typeActivity == 8)
            activityType = activityType.plus("Skating, ")
        else if(typeActivity == 9)
            activityType = activityType.plus("Swimming, ")
        else if(typeActivity == 10)
            activityType = activityType.plus("Mountain Biking, ")
        else if(typeActivity == 11)
            activityType = activityType.plus("Wheelchair, ")
        else if(typeActivity == 12)
            activityType = activityType.plus("Eliptical, ")
        else if(typeActivity == 13)
            activityType = activityType.plus("Other, ")
        else if(typeActivity == 14)
            activityType = activityType.plus("Unknown")

        return activityType
    }

    fun getInput(typeInput:Int):String{
        var inputType=""
        if(typeInput==0)
            inputType = inputType.plus("Manual: ")
        else if(typeInput==1)
            inputType = inputType.plus("GPS: ")
        else if(typeInput==2)
            inputType = inputType.plus("Automatic: ")
        return inputType

    }
}