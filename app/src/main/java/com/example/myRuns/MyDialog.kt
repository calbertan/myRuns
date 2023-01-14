package com.example.myRuns

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class MyDialog: DialogFragment(){
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog

        val builder = AlertDialog.Builder(requireActivity())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_my_dialog, null)
        builder.setView(view)
        builder.setTitle("Pick Profile Picture")

        dialog = builder.create()

        val arrayAdapter: ArrayAdapter<*>
        val users = arrayOf(
            "Camera","Choose from Gallery"
        )

        return dialog
    }
}