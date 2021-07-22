package com.github.sasakitomohiro.rvtablayoutmediator

import com.google.android.material.tabs.TabLayout

interface RvTabConfigurationStrategy {
    fun onConfigureTab(
        tab: TabLayout.Tab,
        position: Int
    )
}
