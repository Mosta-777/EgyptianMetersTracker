package com.mostapps.egyptianmeterstracker.screens.details.meterdetails

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter

class MeterDetailsViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {


    private val mutableMeter = MutableLiveData<DatabaseMeter>()
    val meter: LiveData<DatabaseMeter> get() = mutableMeter


    fun setSelectedMeter(meter: DatabaseMeter) {
        mutableMeter.value = meter
    }


}