package com.example.myRuns

import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.preference.*


class SettingsFragment : PreferenceFragmentCompat(), DialogInterface.OnClickListener,SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)

        PreferenceManager.getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)

    }


    fun onButtonClicked(view: View){
        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_comment, null)
        builder.setView(view)
        builder.setTitle("Comment")
        builder.setPositiveButton("OK", this)
        builder.setNegativeButton("CANCEL", this)
        var dialog: Dialog = builder.create()
    }



    override fun onClick(dialog: DialogInterface, which: Int) {

    }

    //when changing unit preference
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if(key == "Unit_Preference"){
            val prefs = sharedPreferences?.getString(key,"2")
            val temp = findPreference<EditTextPreference>("unit")
            when(prefs?.toInt()){
                1->{
                    temp?.setText("km")
                    //println("debug: ok")
                }
                2->{
                    temp?.setText("miles")
                    //println("debug: o")
                }
            }
        }
    }


}