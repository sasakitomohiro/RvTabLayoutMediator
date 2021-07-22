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
    private var targetPosition = -1
    private var isManualScroll = false

    private val context = recyclerView.context
    private val smoothScroller = TargetSmoothScroller(context)
    private val layoutManager
        get() = recyclerView.layoutManager as LinearLayoutManager

    init {
        require(recyclerView.layoutManager is LinearLayoutManager)
        with(recyclerView) {
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    var prevScrollState = recyclerView.scrollState
                    var currentScrollState = recyclerView.scrollState

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        prevScrollState = currentScrollState
                        currentScrollState = newState
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val currentFirstVisiblePosition =
                            this@RvTabLayoutMediator.layoutManager.findFirstVisibleItemPosition()
                        if (targetPosition != -1 && targetPosition != currentFirstVisiblePosition && currentScrollState != RecyclerView.SCROLL_STATE_IDLE) return else targetPosition =
                            -1
                        val id = adapter!!.getItemId(currentFirstVisiblePosition)
                        val index = findTabIndexByItemId(id)
                        val tab = tabLayout.getTabAt(index)
                        tabLayout.selectTab(tab)
                    }
                }
            )
        }
        with(tabLayout) {
            tabDataProvider.getTabData().forEachIndexed { index, data ->
                addTab(
                    newTab().apply {
                        tabConfigurationStrategy.onConfigureTab(this, index)
                    }
                )
            }
            addOnTabSelectedListener(
                object : TabLayout.OnTabSelectedListener {
                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab ?: return
                        val id = findItemIdByTabIndex(tab.position)
                        val position = findItemPositionByItemId(id)
                        targetPosition = position
                        smoothScroller.targetPosition = targetPosition
                        layoutManager.startSmoothScroll(smoothScroller)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }
                }
            )
        }
    }

    private fun findTabIndexByItemId(id: Long) =
        tabDataProvider.getTabData().indexOfFirst { it.id == id }

    private fun findItemIdByTabIndex(index: Int) = tabDataProvider.getTabData().get(index).id

    private fun findItemPositionByItemId(id: Long) =
        tabDataProvider.getRecyclerViewPositions().indexOfFirst { it == id }
}
