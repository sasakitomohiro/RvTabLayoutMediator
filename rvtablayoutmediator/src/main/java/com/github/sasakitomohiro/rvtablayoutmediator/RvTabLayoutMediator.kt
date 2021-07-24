package com.github.sasakitomohiro.rvtablayoutmediator

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class RvTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val recyclerView: RecyclerView,
    private val tabDataProvider: TabDataProvider,
    private val tabConfigurationStrategy: RvTabConfigurationStrategy
) {
    var attached = false
        private set

    private var targetPosition = -1
    private var isManualScroll = false

    private val context = recyclerView.context
    private val smoothScroller = TargetSmoothScroller(context)
    private val layoutManager
        get() = recyclerView.layoutManager as LinearLayoutManager

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        var prevScrollState = recyclerView.scrollState
        var currentScrollState = recyclerView.scrollState

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            prevScrollState = currentScrollState
            currentScrollState = newState
            isManualScroll = newState != RecyclerView.SCROLL_STATE_IDLE
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentFirstVisiblePosition =
                this@RvTabLayoutMediator.layoutManager.findFirstVisibleItemPosition()
            if (targetPosition != -1 && targetPosition != currentFirstVisiblePosition && currentScrollState != RecyclerView.SCROLL_STATE_IDLE) return else targetPosition =
                -1
            val id = recyclerView.adapter!!.getItemId(currentFirstVisiblePosition)
            val index = findTabIndexByItemId(id)
            val tab = tabLayout.getTabAt(index)
            tabLayout.selectTab(tab)
        }
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab ?: return
            if (isManualScroll) return
            val id = findItemIdByTabIndex(tab.position)
            val position = findItemPositionByItemId(id)
            targetPosition = position
            smoothScroller.targetPosition = targetPosition
            layoutManager.startSmoothScroll(smoothScroller)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }
    }

    fun attach() {
        require(recyclerView.layoutManager is LinearLayoutManager)
        tabDataProvider.getTabData().forEachIndexed { index, data ->
            tabLayout.addTab(
                tabLayout.newTab().apply {
                    tabConfigurationStrategy.onConfigureTab(this, index)
                }
            )
        }
        recyclerView.addOnScrollListener(onScrollListener)
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        recyclerView.scrollToPosition(0)
        attached = true
    }

    fun detach() {
        if (!attached) return
        recyclerView.removeOnScrollListener(onScrollListener)
        tabLayout.removeAllTabs()
        tabLayout.removeOnTabSelectedListener(onTabSelectedListener)
        attached = false
    }

    private fun findTabIndexByItemId(id: Long) =
        tabDataProvider.getTabData().indexOfFirst { it.id == id }

    private fun findItemIdByTabIndex(index: Int) = tabDataProvider.getTabData().get(index).id

    private fun findItemPositionByItemId(id: Long) =
        tabDataProvider.getRecyclerViewPositions().indexOfFirst { it == id }
}
