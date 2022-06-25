package com.mostapps.egyptianmeterstracker.utils

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mostapps.egyptianmeterstracker.base.BaseRecyclerViewAdapter

/**
 * Extension function to setup the RecyclerView
 */
fun <T> RecyclerView.setup(
    adapter: BaseRecyclerViewAdapter<T>
) {
    this.apply {
        layoutManager = LinearLayoutManager(this.context)
        this.adapter = adapter
    }
}

fun Fragment.setTitle(title: String) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
}


fun Spinner.setSpinnerEntries(entries: List<Any>?) {
    if (entries != null) {
        val arrayAdapter = ArrayAdapter(context, R.layout.simple_spinner_item, entries)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter = arrayAdapter
    }
}


fun Spinner.setSpinnerValue(value: Any?) {
    if (adapter != null) {
        val position = (adapter as ArrayAdapter<Any>).getPosition(value)
        setSelection(position, false)
        tag = position
    }
}


fun Spinner.getSpinnerValue(): Any? {
    return selectedItem
}


fun Spinner.setSpinnerInverseBindingListener(listener: InverseBindingListener?) {
    if (listener == null) {
        onItemSelectedListener = null
    } else {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (tag != position) {
                    listener.onChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}


fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            bool
        )
    }
}

//animate changing the view visibility
fun View.fadeIn() {
    this.visibility = View.VISIBLE
    this.alpha = 0f
    this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeIn.alpha = 1f
        }
    })
}

//animate changing the view visibility
fun View.fadeOut() {
    this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            this@fadeOut.alpha = 1f
            this@fadeOut.visibility = View.GONE
        }
    })
}
