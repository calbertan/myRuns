package com.example.myRuns

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment


class StartTab : Fragment() {

    private lateinit var database: RunsDatabase
    private lateinit var databaseDao: RunsDatabaseDao
    private lateinit var repository: RunsRepository
    private lateinit var viewModel: RunsViewModel
    private lateinit var factory: RunsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start_tab, container, false)

        val spinner1:Spinner = view.findViewById(R.id.spinnerInput)
        val spinner2:Spinner = view.findViewById(R.id.spinnerActivity)
        var item = spinner1.getSelectedItem().toString()

        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                item = parent.getItemAtPosition(pos).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val button:Button = view.findViewById(R.id.buttonStart)
        button.setOnClickListener {
            if(item == "Manual Entry") {
                val intent = Intent(requireActivity(), StartEntries::class.java)
                intent.putExtra("INPUT_KEY",spinner1.selectedItemPosition)
                intent.putExtra("ACTIVITY_KEY",spinner2.selectedItemPosition)

                getActivity()?.startActivity(intent)
            }
            else {
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                intent.putExtra("INPUT_KEY",spinner1.selectedItemPosition)
                intent.putExtra("ACTIVITY_KEY",spinner2.selectedItemPosition)
                getActivity()?.startActivity(intent)
            }
        }

        return view
    }



}