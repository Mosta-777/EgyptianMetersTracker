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


    private var preselectedMeter: DatabaseMeter? = null

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
                    if (preselectedMeter != null) {
                        selectedMeter.value = preselectedMeter!!.meterName
                    }

                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
        }
    }

    fun validateAndAddMeterReading() {


        when (val result = enteredMeterReadingIsValid()) {

            //Fetch the currently active meter collection of the selected meter

            is Result.Success<*> -> {
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

                                if (unfinishedMeterReadingsCollection != null) {


                                    //Update the meter reading collection data
                                    //Get Meter Readings of the collection
                                    val readingsResult =
                                        dataSource.getMeterReadingsOfMeterReadingsCollection(
                                            unfinishedMeterReadingsCollection.meterReadingsCollectionId
                                        )
                                    when (readingsResult) {
                                        is Result.Success<*> -> {

                                            val collectionReadings =
                                                (readingsResult.data as MeterReadingsCollectionWithMeterReadings)
                                                    .databaseMeterReadings

                                            val lastRecentReading =
                                                collectionReadings.sortByNewestFirst()
                                                    .first().meterReading
                                            //Make sure the new reading is bigger than or equal last reading

                                            val difference =
                                                meterReading.value?.toInt()!! - lastRecentReading

                                            if (difference < 0) {
                                                showLoading.postValue(false)
                                                showSnackBar.value =
                                                    app.getString(R.string.error_reading_must_not_exceed_last)
                                            } else {
                                                val meterReadingID: String = UUID.randomUUID()
                                                    .toString()
                                                val now = DateUtils.now()
                                                //Save the meter reading

                                                val newMeterReading = DatabaseMeterReading(
                                                    meterReadingId = meterReadingID,
                                                    parentMeterId = selectedMeter.meterId,
                                                    parentMeterCollectionId =
                                                    unfinishedMeterReadingsCollection.meterReadingsCollectionId,
                                                    meterReading = meterReading.value?.toInt()!!,
                                                    readingDate = now
                                                )
                                                dataSource.saveMeterReading(
                                                    newMeterReading
                                                )
                                                //Update last meter reading date of meter
                                                dataSource.updateMeterLastReadingDate(
                                                    DatabaseMeterLastReadingDateUpdate(
                                                        selectedMeter.meterId,
                                                        now
                                                    )
                                                )
                                                val mutableReadings = collectionReadings.toMutableList()
                                                mutableReadings.add(newMeterReading)

                                                val machineOutput =
                                                    MeterTariffMachine.processMeterReadings(
                                                        mutableReadings.sortByOldestFirst().toList(),
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

                                        }
                                        is Result.Error -> {
                                            showLoading.postValue(false)
                                            showSnackBar.value =
                                                app.getString(R.string.error_general)
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

            is Result.Error -> showSnackBar.value = result.message

        }


    }

    private fun enteredMeterReadingIsValid(): Result<Boolean> {


        if (meterReading.value.isNullOrEmpty()
        ) return Result.Error(app.getString(R.string.please_enter_all_fields))

        if (meterReading.value!!.toIntOrNull() == null)
            return Result.Error(app.getString(R.string.please_enter_valid_readings))

        return Result.Success(true)
    }

    fun setSelectedMeter(meter: DatabaseMeter) {
        preselectedMeter = meter
    }
}