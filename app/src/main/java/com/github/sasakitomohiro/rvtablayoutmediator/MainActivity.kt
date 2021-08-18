package com.github.sasakitomohiro.rvtablayoutmediator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ActivityMainBinding
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleBinding
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleHeaderBinding
import com.google.android.material.tabs.TabLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.viewbinding.BindableItem

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    private val sampleAdapter = SampleAdapter()
    private val items = listOf(
        1L,
        2L,
        3L,
        4L,
        5L,
        6L,
        7L,
        8L,
        9L
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = sampleAdapter
        sampleAdapter.update(items)

        RvTabLayoutMediator(
            tabLayout = binding.tab,
            recyclerView = binding.recycler,
            tabDataProvider = sampleAdapter,
            tabConfigurationStrategy = object : RvTabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    tab.text = items[position].toString()
                }
            }
        ).attach()
    }
}
