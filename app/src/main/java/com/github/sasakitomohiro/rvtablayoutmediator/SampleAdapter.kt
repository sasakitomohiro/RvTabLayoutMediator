package com.github.sasakitomohiro.rvtablayoutmediator

import android.view.View
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleBinding
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleHeaderBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import com.xwray.groupie.viewbinding.BindableItem

class SampleAdapter : GroupAdapter<GroupieViewHolder>(), TabDataProvider {
    private val sampleItems = mutableListOf<Section>()
    private val tabData = mutableListOf<TabData>()
    private val itemPositions = mutableListOf<Long>()

    fun update(items: List<Long>) {
        sampleItems.clear()
        tabData.clear()
        itemPositions.clear()

        items.forEach { sectionId ->
            sampleItems.add(
                Section().apply {
                    val header = SampleHeaderItem(sectionId, sectionId.toString())
                    setHeader(header)
                    tabData.add(TabData(header.id, header.title))
                    itemPositions.add(header.id)
                    val sampleItem = SampleItem("item")
                    add(sampleItem)
                    itemPositions.add(sampleItem.id)
                }
            )
        }
        update(sampleItems)
    }


    override fun getTabData(): List<TabData> = tabData

    override fun getRecyclerViewPositions(): List<Long> = itemPositions
}

class SampleHeaderItem(
    id: Long,
    val title: String
) : BindableItem<ItemSampleHeaderBinding>(id) {
    override fun getLayout(): Int = R.layout.item_sample_header

    override fun initializeViewBinding(view: View) = ItemSampleHeaderBinding.bind(view)

    override fun bind(viewBinding: ItemSampleHeaderBinding, position: Int) {
        viewBinding.title.text = title
    }
}

class SampleItem(
    val title: String
) : BindableItem<ItemSampleBinding>() {
    override fun getLayout(): Int = R.layout.item_sample

    override fun initializeViewBinding(view: View) = ItemSampleBinding.bind(view)

    override fun bind(viewBinding: ItemSampleBinding, position: Int) {
        viewBinding.title.text = title
    }
}
