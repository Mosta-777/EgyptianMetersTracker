package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mostapps.egyptianmeterstracker.databinding.ReadingListItemBinding
import com.mostapps.egyptianmeterstracker.models.MeterReadingListItem

class ReadingsListAdapter :
    ListAdapter<MeterReadingListItem,
            ReadingsListAdapter.ReadingsViewHolder>(ReadingsDiffCallback()) {


    override fun onBindViewHolder(holderReadings: ReadingsViewHolder, position: Int) {
        val item = getItem(position)

        holderReadings.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingsViewHolder {
        return ReadingsViewHolder.from(parent)
    }

    class ReadingsViewHolder private constructor(val binding: ReadingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MeterReadingListItem) {
            binding.reading = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReadingsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReadingListItemBinding.inflate(layoutInflater, parent, false)
                return ReadingsViewHolder(binding)
            }
        }
    }

}

class ReadingsDiffCallback : DiffUtil.ItemCallback<MeterReadingListItem>() {
    override fun areItemsTheSame(
        oldItem: MeterReadingListItem,
        newItem: MeterReadingListItem
    ): Boolean {
        return oldItem.readingNumber == newItem.readingNumber
    }

    override fun areContentsTheSame(
        oldItem: MeterReadingListItem,
        newItem: MeterReadingListItem
    ): Boolean {
        return oldItem == newItem
    }
}

class ReadingsListener(val clickListener: (reading: MeterReadingListItem) -> Unit) {
    fun onClick(reading: MeterReadingListItem) = clickListener(reading)
}