package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostapps.egyptianmeterstracker.databinding.MeterReadingsCollectionListItemBinding
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem

class MeterReadingsCollectionListAdapter(private val clickListener: CollectionsListener) :
    ListAdapter<MeterReadingsCollectionListItem,
            MeterReadingsCollectionListAdapter.CollectionsViewHolder>(CollectionsDiffCallback()) {


    override fun onBindViewHolder(collectionsViewHolder: CollectionsViewHolder, position: Int) {
        val item = getItem(position)

        collectionsViewHolder.bind(clickListener, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsViewHolder {
        return CollectionsViewHolder.from(parent)
    }

    class CollectionsViewHolder private constructor(val binding: MeterReadingsCollectionListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: CollectionsListener, item: MeterReadingsCollectionListItem) {
            binding.clickListener = clickListener
            binding.item = item
            binding.readingsTable.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            val nestedAdapter = ReadingsListAdapter()
            nestedAdapter.submitList(item.nestedMeterReadingsListItems)
            binding.recyclerViewReadings.adapter = nestedAdapter
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CollectionsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    MeterReadingsCollectionListItemBinding.inflate(layoutInflater, parent, false)
                return CollectionsViewHolder(binding)
            }
        }
    }

}

class CollectionsDiffCallback : DiffUtil.ItemCallback<MeterReadingsCollectionListItem>() {
    override fun areItemsTheSame(
        oldItem: MeterReadingsCollectionListItem,
        newItem: MeterReadingsCollectionListItem
    ): Boolean {
        return oldItem.collectionId === newItem.collectionId
    }

    override fun areContentsTheSame(
        oldItem: MeterReadingsCollectionListItem,
        newItem: MeterReadingsCollectionListItem
    ): Boolean {
        return oldItem == newItem
    }
}

class CollectionsListener(val clickListener: (collection: MeterReadingsCollectionListItem) -> Unit) {
    fun onClick(collection: MeterReadingsCollectionListItem) = clickListener(collection)
}