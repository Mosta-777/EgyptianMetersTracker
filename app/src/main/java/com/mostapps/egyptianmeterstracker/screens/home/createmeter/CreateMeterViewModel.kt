package com.mostapps.egyptianmeterstracker.screens.home.createmeter

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO
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


    fun validateAndCreateMeter() {
        when (val result = validateMeterData()) {
            is Result.Success<*> -> createMeter()
            is Result.Error -> showSnackBar.value = result.message
        }
    }


    fun validateMeterData(): Result<Boolean> {

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
            dataSource.saveMeter(
                meter = MeterDTO(
                    meterId = meterID,
                    meterName = meterName.value,
                    meterType = meterType.value,
                    lastReadingDate = DateUtils.now()
                ),
                firstMeterReading = MeterReadingDTO(
                    parentMeterId = meterID,
                    meterReading = Integer.parseInt(firstMeterReading.value!!),
                    readingDate = DateUtils.formatDate(
                        firstMeterReadingDate.value,
                        DateUtils.DEFAULT_FORMAT_DATE
                    )
                ),
                currentMeterReading = MeterReadingDTO(
                    parentMeterId = meterID,
                    meterReading = Integer.parseInt(currentMeterReading.value!!),
                    readingDate = DateUtils.now()
                )

            )
            showLoading.postValue(false)
            showToast.value = app.getString(R.string.meter_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }


}