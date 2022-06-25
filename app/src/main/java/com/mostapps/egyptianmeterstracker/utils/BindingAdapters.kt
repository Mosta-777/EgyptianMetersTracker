package com.mostapps.egyptianmeterstracker.utils

import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.mostapps.egyptianmeterstracker.MeterTypes
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseRecyclerViewAdapter


    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }


    @BindingAdapter("entries")
    fun Spinner.setEntries(entries: List<Any>?) {
        setSpinnerEntries(entries)
    }



    @BindingAdapter("selectedValue")
    fun Spinner.setSelectedValue(selectedValue: Any?) {
        setSpinnerValue(selectedValue)
    }

    @BindingAdapter("selectedValueAttrChanged")
    fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
        setSpinnerInverseBindingListener(inverseBindingListener)
    }

    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    fun Spinner.getSelectedValue(): Any? {
        return getSpinnerValue()
    }


    //<a target="_blank" href="https://icons8.com/icon/ns9lhuct4dg4/water">Water</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
    //<a target="_blank" href="https://icons8.com/icon/dDgWKgq91sx2/gas">Gas</a> icon by <a target="_blank" href="https://icons8.com">Icons8</a>
    @BindingAdapter("meterTypeIcon")
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
