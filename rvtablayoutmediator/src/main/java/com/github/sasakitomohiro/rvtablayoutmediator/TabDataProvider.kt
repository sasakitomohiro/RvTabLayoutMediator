package com.github.sasakitomohiro.rvtablayoutmediator

interface TabDataProvider {
    fun getTabData(): List<TabData>

    fun getRecyclerViewPositions(): List<Long>
}
