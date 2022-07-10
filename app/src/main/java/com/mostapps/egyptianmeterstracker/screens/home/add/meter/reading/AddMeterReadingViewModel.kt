package com.mostapps.egyptianmeterstracker.screens.home.add.meter.reading

import android.app.Application
import androidx.lifecycle.*
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.base.NavigationCommand
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.entites.*
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterReadingsCollectionWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import com.mostapps.egyptianmeterstracker.utils.MeterTariffMachine
import com.mostapps.egyptianmeterstracker.utils.Result
import kotlinx.coroutines.launch
import java.util.*

class AddMeterReadingViewModel(
    val app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {


    private val metersList = MutableLiveData<List<DatabaseMeter>>()

    val meterListNames: LiveData<List<String>> =
        Transformations.map(metersList) { it.map { meter -> meter.meterName!! } }


    val selectedMeter = MutableLiveData<String>()
    val meterReading = MutableLiveData<String>()

    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getMeters()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val fetchedMeters = result.data as List<DatabaseMeter>
                    metersList.postValue(fetchedMeters)
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    fun validateAndAddMeterReading() {


        if (enteredMeterReadingIsValid()) {

            //Fetch the currently active meter collection of the selected meter

            val selectedMeter =
                metersList.value?.firstOrNull { it.meterName == selectedMeter.value }

            if (selectedMeter != null) {

                //use the selected meter id to get unfinished meter collection

                showLoading.value = true
                viewModelScope.launch {
                    val allMeterCollectionsResult =
                        dataSource.getMeterReadingsCollectionsOfMeter(selectedMeter.meterId)
                    when (allMeterCollectionsResult) {
                        is Result.Success<*> -> {
                            val allMeterCollections =
                                allMeterCollectionsResult.data as MeterWithMeterReadingsCollections
                            //Get the unfinished meter collection

                            val unfinishedMeterReadingsCollection =
                                allMeterCollections.meterCollections.sortByNewestFirst()
                                    .firstOrNull { it.isFinished == false }

                            //Insert the meter reading

                            if (unfinishedMeterReadingsCollection != null) {

                                val meterReadingID: String = UUID.randomUUID()
                                    .toString()
                                val now = DateUtils.now()
                                //Save the meter reading
                                dataSource.saveMeterReading(
                                    DatabaseMeterReading(
                                        meterReadingId = meterReadingID,
                                        parentMeterId = selectedMeter.meterId,
                                        parentMeterCollectionId =
                                        unfinishedMeterReadingsCollection.meterReadingsCollectionId,
                                        meterReading = meterReading.value?.toInt()!!,
                                        readingDate = now
                                    )
                                )
                                //Update the meter reading collection data

                                //Get Meter Readings of the collection
                                val result =
                                    dataSource.getMeterReadingsOfMeterReadingsCollection(
                                        unfinishedMeterReadingsCollection.meterReadingsCollectionId
                                    )
                                when (result) {
                                    is Result.Success<*> -> {

                                        val collectionReadings =
                                            (result.data as MeterReadingsCollectionWithMeterReadings)
                                                .databaseMeterReadings.sortByOldestFirst()

                                        val machineOutput = MeterTariffMachine.processMeterReadings(
                                            collectionReadings,
                                            selectedMeter.meterType,
                                            selectedMeter.meterSubType
                                        )

                                        dataSource.updateCollectionMainData(
                                            DatabaseMeterReadingsCollectionMainDataUpdate(
                                                meterReadingsCollectionId = unfinishedMeterReadingsCollection.meterReadingsCollectionId,
                                                collectionCurrentSlice = machineOutput.currentSlice.meterSliceValue,
                                                totalConsumption = machineOutput.totalConsumption,
                                                totalCost = machineOutput.totalCost
                                            )
                                        )

                                        showLoading.postValue(false)
                                        showToast.postValue(app.getString(R.string.meter_reading_saved))
                                        navigationCommand.postValue(NavigationCommand.Back)
                                    }
                                    is Result.Error -> {
                                        showLoading.postValue(false)
                                        showSnackBar.value = app.getString(R.string.error_general)
                                    }
                                }
                            } else {
                                showLoading.postValue(false)
                                showSnackBar.value = app.getString(R.string.error_general)
                            }

                        }
                        is Result.Error -> {
                            showLoading.postValue(false)
                            showSnackBar.value = allMeterCollectionsResult.message
                        }
                    }
                }


                //Insert the entered meter reading


            }
        }


    }

    private fun enteredMeterReadingIsValid(): Boolean {

        //TODO add validation, validations i can think of, meter reading should not be empty
        //Also the meter readings should be numbers only
        return true
    }

}