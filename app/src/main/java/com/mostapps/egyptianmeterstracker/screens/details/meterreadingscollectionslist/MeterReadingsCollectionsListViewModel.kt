package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import com.mostapps.egyptianmeterstracker.utils.Result
import kotlinx.coroutines.launch

class MeterReadingsCollectionsListViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {

    private val mutableMeter = MutableLiveData<DatabaseMeter>()
    val meter: LiveData<DatabaseMeter> get() = mutableMeter


    val meterName = meter.map { meter -> meter.meterName }


    var meterReadingCollections = emptyList<DatabaseMeterReadingsCollection>()

    val metersReadingsCollectionListItems = MutableLiveData<List<MeterReadingsCollectionListItem>>()


    fun setSelectedMeter(meter: DatabaseMeter) {
        mutableMeter.value = meter
        loadMeterCollections()
    }


    private fun loadMeterCollections() {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getMeterReadingsCollectionsOfMeter(meter.value!!.meterId)
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    meterReadingCollections =
                        (result.data as MeterWithMeterReadingsCollections).run {
                            meterCollections
                        }
                    val dataList = ArrayList<MeterReadingsCollectionListItem>().apply {
                        addAll((meterReadingCollections).map { meterReadingCollection ->
                            MeterReadingsCollectionListItem(
                                startDate = DateUtils.formatDate(
                                    meterReadingCollection.collectionStartDate,
                                    DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                                ),
                                endDate = meterReadingCollection.collectionEndDate?.let {
                                    DateUtils.formatDate(
                                        it,
                                        DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                                    )
                                },
                                currentMeterSlice = meterReadingCollection.collectionCurrentSlice
                            )
                        })
                    }
                    metersReadingsCollectionListItems.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    private fun invalidateShowNoData() {
    }


}