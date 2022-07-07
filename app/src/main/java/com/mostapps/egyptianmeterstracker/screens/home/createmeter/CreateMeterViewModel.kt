package com.mostapps.egyptianmeterstracker.screens.home.createmeter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.*
import com.mostapps.egyptianmeterstracker.utils.Result

class CreateMeterViewModel(
    val app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {

    val meterName = MutableLiveData<String>()
    val firstMeterReading = MutableLiveData<String>()
    val firstMeterReadingDate = MutableLiveData<String>()
    val currentMeterReading = MutableLiveData<String>()
    val meterType = MutableLiveData<Int>()
    val meterSubType = MutableLiveData<Int>()

    fun validateAndCreateMeter() {
        when (val result = validateMeterData()) {
            is Result.Success<*> -> createMeter()
            is Result.Error -> showSnackBar.value = result.message
        }
    }


    private fun validateMeterData(): Result<Boolean> {

        //TODO add any needed validation code in here
        //Validations i am thinking of right now:
        //1- Current reading date should be after first reading date and the difference
        //between them shouldn't exceed 31 days max
        //2- Difference between two readings shouldn't exceed certain limit

        return Result.Success(true)

    }

    private fun createMeter() {
        showLoading.value = true

        viewModelScope.launch {

            val meterID: String = UUID.randomUUID()
                .toString()
            val meterReadingsCollectionID = UUID.randomUUID()
                .toString()

            val now = DateUtils.now()

            dataSource.saveMeter(
                databaseMeter = DatabaseMeter(
                    meterId = meterID,
                    meterName = meterName.value!!,
                    meterType = meterType.value!!,
                    meterSubType = meterSubType.value!!,
                    lastReadingDate = now
                ),
                meterReadingsCollection = DatabaseMeterReadingsCollection(
                    meterReadingsCollectionId = meterReadingsCollectionID,
                    parentMeterId = meterID,
                    collectionStartDate = now,
                    //TODO look into the collection slice
                    collectionCurrentSlice = 1
                ),
                firstDatabaseMeterReading = DatabaseMeterReading(
                    parentMeterId = meterID,
                    meterReading = Integer.parseInt(firstMeterReading.value!!),
                    readingDate = DateUtils.formatDate(
                        firstMeterReadingDate.value,
                        DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                    ),
                    parentMeterCollectionId = meterReadingsCollectionID
                ),
                currentDatabaseMeterReading = DatabaseMeterReading(
                    parentMeterId = meterID,
                    meterReading = Integer.parseInt(currentMeterReading.value!!),
                    readingDate = now,
                    parentMeterCollectionId = meterReadingsCollectionID
                )

            )
            showLoading.postValue(false)
            showToast.postValue(app.getString(R.string.meter_saved))
            navigationCommand.postValue(NavigationCommand.Back)
        }
    }


}