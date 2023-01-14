package com.example.myRuns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(){

    private lateinit var start: StartTab
    private lateinit var history: historyTab
    private lateinit var settings: SettingsFragment
    private lateinit var fragments: ArrayList<Fragment>
    private lateinit var tabLayout: TabLayout
    private lateinit var viewpager: ViewPager2
    private lateinit var myFragmentStateAdapter: MyFragmentPageAdapter
    private val tabTitles = arrayOf("start","history","settings")
    private lateinit var tabConfigurationStrategy : TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator:TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start = StartTab()
        history = historyTab()
        settings = SettingsFragment()
        fragments = ArrayList()
        fragments.add(start)
        fragments.add(history)
        fragments.add(settings)

        tabLayout = findViewById(R.id.tablayout)
        viewpager = findViewById(R.id.viewpager)
        myFragmentStateAdapter = MyFragmentPageAdapter(this,fragments)
        viewpager.adapter = myFragmentStateAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy(){
            tab: TabLayout.Tab, position: Int ->
            tab.text = tabTitles[position]
        }

        tabLayoutMediator = TabLayoutMediator(tabLayout, viewpager, tabConfigurationStrategy)
        tabLayoutMediator.attach()

        //asks user for location permission

    }

    override fun onDestroy(){
        super.onDestroy()
        tabLayoutMediator.detach()
    }

}
