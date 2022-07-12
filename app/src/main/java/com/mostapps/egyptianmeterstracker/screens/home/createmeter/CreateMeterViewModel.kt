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
import com.mostapps.egyptianmeterstracker.utils.MeterTariffMachine
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

        if (meterName.value.isNullOrEmpty() ||
            firstMeterReading.value.isNullOrEmpty() ||
            firstMeterReadingDate.value.isNullOrEmpty() ||
            currentMeterReading.value.isNullOrEmpty()
        ) return Result.Error(app.getString(R.string.please_enter_all_fields))


        if (firstMeterReading.value!!.toIntOrNull() == null
            || currentMeterReading.value!!.toIntOrNull() == null
        )
            return Result.Error(app.getString(R.string.please_enter_valid_readings))

        val readingsDifference =
            Integer.parseInt(currentMeterReading.value!!) - Integer.parseInt(firstMeterReading.value!!)

        if (readingsDifference < 0)
            return Result.Error(app.getString(R.string.error_reading_difference))

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


            val firstDatabaseMeterReading = DatabaseMeterReading(
                parentMeterId = meterID,
                meterReading = Integer.parseInt(firstMeterReading.value!!),
                readingDate = DateUtils.formatDate(
                    firstMeterReadingDate.value,
                    DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                ) ?: Date(),
                parentMeterCollectionId = meterReadingsCollectionID
            )

            val currentDatabaseMeterReading = DatabaseMeterReading(
                parentMeterId = meterID,
                meterReading = Integer.parseInt(currentMeterReading.value!!),
                readingDate = now,
                parentMeterCollectionId = meterReadingsCollectionID
            )


            val machineOutput = MeterTariffMachine.processMeterReadings(
                listOf(firstDatabaseMeterReading, currentDatabaseMeterReading),
                meterType.value!!,
                meterSubType.value!!
            )

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
                    collectionCurrentSlice = machineOutput.currentSlice.meterSliceValue,
                    totalConsumption = machineOutput.totalConsumption,
                    totalCost = machineOutput.totalCost,
                    isFinished = false
                ),
                firstDatabaseMeterReading = firstDatabaseMeterReading,
                currentDatabaseMeterReading = currentDatabaseMeterReading

            )
            showLoading.postValue(false)
            showToast.postValue(app.getString(R.string.meter_saved))
            navigationCommand.postValue(NavigationCommand.Back)
        }
    }


}