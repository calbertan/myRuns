package com.example.myRuns

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentPageAdapter(fragment: FragmentActivity, var inputList: ArrayList<Fragment>): FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return inputList[position]
    }

    override fun getItemCount():Int{
        return inputList.size
    }
}