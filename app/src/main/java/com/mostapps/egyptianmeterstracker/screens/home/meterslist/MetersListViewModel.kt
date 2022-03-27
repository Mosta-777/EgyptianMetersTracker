package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import kotlinx.coroutines.launch
import com.mostapps.egyptianmeterstracker.data.dto.Result
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.utils.DateUtils


class MetersListViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {

    val metersList = MutableLiveData<List<MeterDataListItem>>()


    /**
     * Get all the meters from the DataSource and add them to the remindersList to be shown on the UI,
     * or show error if any
     */
    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {
            //interacting with the dataSource has to be through a coroutine
            val result = dataSource.getMeters()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<MeterDataListItem>()
                    dataList.addAll((result.data as List<MeterDTO>).map { meter ->
                        //map the meter data from the DB to the be ready to be displayed on the UI
                        MeterDataListItem(
                            name = meter.meterName,
                            lastRecordedReadingDate = DateUtils.formatDate(
                                meter.lastReadingDate,
                                DateUtils.DEFAULT_FORMAT_DATE
                            ),
                            meterType = meter.meterType
                        )
                    })
                    metersList.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    private fun invalidateShowNoData() {
        showNoData.value = metersList.value == null || metersList.value!!.isEmpty()
    }
}