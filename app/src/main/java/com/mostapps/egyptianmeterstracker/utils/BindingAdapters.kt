package com.mostapps.egyptianmeterstracker.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mostapps.egyptianmeterstracker.MeterTypes
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseRecyclerViewAdapter


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }


    //<a target="_blank" href="https://icons8.com/icon/ns9lhuct4dg4/water">Water</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
    //<a target="_blank" href="https://icons8.com/icon/dDgWKgq91sx2/gas">Gas</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
    @BindingAdapter("meterTypeIcon")
    @JvmStatic
    fun bindMeterTypeIcon(imageView: ImageView, meterType: Int) {
        when (meterType) {
            MeterTypes.ELECTRICITY.meterValue -> imageView.setImageResource(R.drawable.ic_electricity_64)
            MeterTypes.WATER.meterValue -> imageView.setImageResource(R.drawable.ic_water_64)
            MeterTypes.GAS.meterValue -> imageView.setImageResource(R.drawable.ic_gas_64)
        }
    }


    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }
}