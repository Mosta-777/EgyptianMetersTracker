package com.mostapps.egyptianmeterstracker.screens.details.collectorarrived

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

class CollectorArrivedViewModel(
    val app: Application,
    private val dataSource: MetersDataSource,
) : BaseViewModel(app) {

    val collectorMeterReading = MutableLiveData<String>()
    val firstMeterReadingDate = MutableLiveData<String>()
    val currentMeterReading = MutableLiveData<String>()

    var meter: DatabaseMeter? = null


    fun setSelectedMeter(meter: DatabaseMeter) {
        this.meter = meter
    }

    fun closeCurrentCollectionAndStartNewOne() {


        when (val isValidReadings = validateReadings()) {
            is Result.Success<*> -> {
                if (meter != null) {
                    showLoading.value = true
                    viewModelScope.launch {
                        //Get the current collection meter readings add the last one to them
                        //feed them to the machine to get the updated data for the collection

                        val allMeterCollectionsResult =
                            dataSource.getMeterReadingsCollectionsOfMeter(meter!!.meterId)
                        when (allMeterCollectionsResult) {
                            is Result.Success<*> -> {
                                val allMeterCollections =
                                    allMeterCollectionsResult.data as MeterWithMeterReadingsCollections
                                //Get the unfinished meter collection

                                val unfinishedMeterReadingsCollection =
                                    allMeterCollections.meterCollections.sortByNewestFirst()
                                        .firstOrNull { it.isFinished == false }

                                if (unfinishedMeterReadingsCollection != null) {
                                    val now = DateUtils.now()

                                    //Pass this
                                    val finishedCollectionCollectorReading = DatabaseMeterReading(
                                        meterReadingId = UUID.randomUUID()
                                            .toString(),
                                        parentMeterId = meter!!.meterId,
                                        parentMeterCollectionId =
                                        unfinishedMeterReadingsCollection.meterReadingsCollectionId,
                                        meterReading = collectorMeterReading.value?.toInt()!!,
                                        readingDate = now
                                    )


                                    val readingsResult =
                                        dataSource.getMeterReadingsOfMeterReadingsCollection(
                                            unfinishedMeterReadingsCollection.meterReadingsCollectionId
                                        )
                                    when (readingsResult) {
                                        is Result.Success<*> -> {
                                            val mutableCollectionReadings =
                                                (readingsResult.data as MeterReadingsCollectionWithMeterReadings).databaseMeterReadings.toMutableList()
                                            mutableCollectionReadings.add(
                                                finishedCollectionCollectorReading
                                            )
                                            val collectionReadings =
                                                mutableCollectionReadings.toList()
                                                    .sortByOldestFirst()

                                            //Pass this
                                            val finishedCollectionMainData =
                                                MeterTariffMachine.processMeterReadings(
                                                    collectionReadings,
                                                    meter!!.meterType,
                                                    meter!!.meterSubType
                                                )

                                            val updatedData =
                                                DatabaseMeterReadingsCollectionMainDataUpdate(
                                                    meterReadingsCollectionId = unfinishedMeterReadingsCollection.meterReadingsCollectionId,
                                                    collectionCurrentSlice = finishedCollectionMainData.currentSlice.meterSliceValue,
                                                    totalCost = finishedCollectionMainData.totalCost,
                                                    totalConsumption = finishedCollectionMainData.totalConsumption
                                                )

                                            val newCollectionId = UUID.randomUUID()
                                                .toString()

                                            //Pass This
                                            val newCollectionCollectorReading =
                                                DatabaseMeterReading(
                                                    meterReadingId = UUID.randomUUID()
                                                        .toString(),
                                                    parentMeterId = meter!!.meterId,
                                                    parentMeterCollectionId = newCollectionId,
                                                    meterReading = collectorMeterReading.value?.toInt()!!,
                                                    readingDate = now
                                                )

                                            //Pass this
                                            val newCollectionCurrentReading = DatabaseMeterReading(
                                                meterReadingId = UUID.randomUUID()
                                                    .toString(),
                                                parentMeterId = meter!!.meterId,
                                                parentMeterCollectionId = newCollectionId,
                                                meterReading = currentMeterReading.value?.toInt()!!,
                                                readingDate = now
                                            )


                                            val newCollectionMainData =
                                                MeterTariffMachine.processMeterReadings(
                                                    listOf(
                                                        newCollectionCollectorReading,
                                                        newCollectionCurrentReading
                                                    ),
                                                    meter!!.meterType,
                                                    meter!!.meterSubType
                                                )

                                            //Pass this
                                            val newMeterCollection =
                                                DatabaseMeterReadingsCollection(
                                                    meterReadingsCollectionId = newCollectionId,
                                                    parentMeterId = meter!!.meterId,
                                                    collectionStartDate = now,
                                                    collectionCurrentSlice = newCollectionMainData.currentSlice.meterSliceValue,
                                                    totalConsumption = newCollectionMainData.totalConsumption,
                                                    totalCost = newCollectionMainData.totalCost,
                                                    isFinished = false
                                                )


                                            dataSource.updateCollectionIsFinished(
                                                finishedCollectionCollectorReading = finishedCollectionCollectorReading,
                                                collectorArrivalDate = firstMeterReadingDate.value!!,
                                                finishedCollectionTerminationData = updatedData,
                                                newCollectionCollectorReading = newCollectionCollectorReading,
                                                newCollectionCurrentReading = newCollectionCurrentReading,
                                                newMeterReadingsCollection = newMeterCollection
                                            )

                                            showLoading.postValue(false)
                                            showToast.postValue(app.getString(R.string.new_collection_started))
                                            navigationCommand.postValue(NavigationCommand.Back)

                                        }
                                        is Result.Error -> {
                                            showLoading.postValue(false)
                                            showSnackBar.value = readingsResult.message
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
                }
            }

            is Result.Error -> {
                showLoading.postValue(false)
                showSnackBar.value = isValidReadings.message
            }
        }
    }

    private fun validateReadings(): Result<Boolean> {
        if (
            collectorMeterReading.value.isNullOrEmpty() ||
            firstMeterReadingDate.value.isNullOrEmpty() ||
            currentMeterReading.value.isNullOrEmpty()
        ) return Result.Error(app.getString(R.string.please_enter_all_fields))

        if (collectorMeterReading.value!!.toIntOrNull() == null
            || currentMeterReading.value!!.toIntOrNull() == null
        )
            return Result.Error(app.getString(R.string.please_enter_valid_readings))

        val readingsDifference =
            Integer.parseInt(currentMeterReading.value!!) - Integer.parseInt(collectorMeterReading.value!!)

        if (readingsDifference < 0)
            return Result.Error(app.getString(R.string.error_reading_difference))


        return Result.Success(true)

    }


}