package com.github.sasakitomohiro.rvtablayoutmediator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ActivityMainBinding
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleBinding
import com.github.sasakitomohiro.rvtablayoutmediator.databinding.ItemSampleHeaderBinding
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
        5L
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = sampleAdapter
        sampleAdapter.update(items)

        RvTabLayoutMediator(
            binding.tab,
            binding.recycler,
            sampleAdapter
        )
    }
}

class SampleAdapter : GroupAdapter<GroupieViewHolder>(), TabDataProvider {
    private val sampleItems = mutableListOf<Section>()

    fun update(items: List<Long>) {
        sampleItems.clear()
        sampleItems.addAll(
            items.map {
                Section().apply {
                    setHeader(
                        SampleHeaderItem(it, it.toString())
                    )
                    this.add(
                        SampleItem("item")
                    )
                }
            }
        )
        update(sampleItems)
    }

    override fun getTabData(): List<TabData> = listOf()
}

class SampleHeaderItem(
    id: Long,
    private val title: String
) : BindableItem<ItemSampleHeaderBinding>(id) {
    override fun getLayout(): Int = R.layout.item_sample_header

    override fun initializeViewBinding(view: View) = ItemSampleHeaderBinding.bind(view)

    override fun bind(viewBinding: ItemSampleHeaderBinding, position: Int) {
        viewBinding.title.text = title
    }
}

class SampleItem(
    private val title: String
) : BindableItem<ItemSampleBinding>() {
    override fun getLayout(): Int = R.layout.item_sample

    override fun initializeViewBinding(view: View) = ItemSampleBinding.bind(view)

    override fun bind(viewBinding: ItemSampleBinding, position: Int) {
        viewBinding.title.text = title
    }
}
