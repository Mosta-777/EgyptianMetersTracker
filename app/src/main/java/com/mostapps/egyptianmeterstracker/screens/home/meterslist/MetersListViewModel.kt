package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import kotlinx.coroutines.launch

class MetersListViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {
    // list that holds the reminder data to be displayed on the UI
    val metersList = MutableLiveData<List<MeterDataListItem>>()

    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {

        }
    }

    private fun invalidateShowNoData() {
        showNoData.value = metersList.value == null || metersList.value!!.isEmpty()
    }
}