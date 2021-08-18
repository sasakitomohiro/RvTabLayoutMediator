package com.github.sasakitomohiro.rvtablayoutmediator

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

private const val NO_TARGET_POSITION = -1

class RvTabLayoutMediator(
    private val tabLayout: TabLayout,
    private val recyclerView: RecyclerView,
    private val tabDataProvider: TabDataProvider,
    private val smoothScroller: RecyclerView.SmoothScroller = TargetSmoothScroller(recyclerView.context),
    private val tabConfigurationStrategy: RvTabConfigurationStrategy
) {
    var attached = false
        private set

    private var targetPosition = NO_TARGET_POSITION
    private var isManualScroll = false

    private val layoutManager
        get() = recyclerView.layoutManager as LinearLayoutManager

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        var prevScrollState = recyclerView.scrollState
        var currentScrollState = recyclerView.scrollState

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            prevScrollState = currentScrollState
            currentScrollState = newState
            isManualScroll = newState != RecyclerView.SCROLL_STATE_IDLE
            if (currentScrollState == RecyclerView.SCROLL_STATE_IDLE && targetPosition != NO_TARGET_POSITION) {
                targetPosition = NO_TARGET_POSITION
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val currentFirstVisiblePosition =
                this@RvTabLayoutMediator.layoutManager.findFirstVisibleItemPosition()
            if (targetPosition != NO_TARGET_POSITION &&
                targetPosition != currentFirstVisiblePosition &&
                currentScrollState != RecyclerView.SCROLL_STATE_IDLE
            ) return else targetPosition = NO_TARGET_POSITION
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
