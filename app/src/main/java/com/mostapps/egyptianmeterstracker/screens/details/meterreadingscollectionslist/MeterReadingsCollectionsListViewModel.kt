package com.mostapps.egyptianmeterstracker.screens.details.meterreadingscollectionslist

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseStorageInterface
import com.mostapps.egyptianmeterstracker.models.MeterReadingsCollectionListItem
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import com.mostapps.egyptianmeterstracker.utils.MeterTariffMachine
import com.mostapps.egyptianmeterstracker.utils.Result
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent
import java.util.*
import kotlin.collections.ArrayList

class MeterReadingsCollectionsListViewModel(
    app: Application,
    private val dataSource: MetersDataSource,
    private val storageManager: FirebaseStorageInterface
) : BaseViewModel(app) {

    private val mutableMeter = MutableLiveData<DatabaseMeter>()
    val meter: LiveData<DatabaseMeter> get() = mutableMeter


    val meterName = meter.map { meter -> meter.meterName }


    var meterReadingCollections = emptyList<DatabaseMeterReadingsCollection>()

    val metersReadingsCollectionListItems = MutableLiveData<List<MeterReadingsCollectionListItem>>()


    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by KoinJavaComponent.inject(FirebaseAuthenticationManager::class.java)


    private val authenticatedUser: FirebaseUser? =
        firebaseAuthenticationManager.getCurrentUser()


    fun getMeterImageStorageReference(): StorageReference? {
        authenticatedUser?.run {
            return storageManager.getMeterImageStorageReference(uid, meter.value!!.meterId)
        }
        return null
    }

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
                                ) ?: "-",
                                endDate = meterReadingCollection.collectionEndDate?.let {
                                    DateUtils.formatDate(
                                        it,
                                        DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                                    ) ?: "-"
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

    fun handleOnMeterCollectionClicked(position: Int) {
        showLoading.value = true
        val clickedMeterCollectionId = meterReadingCollections[position].meterReadingsCollectionId
        viewModelScope.launch {
            val result =
                dataSource.getMeterReadingsOfMeterReadingsCollection(clickedMeterCollectionId)
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    /*val meterReadings =
                        (result.data as MeterReadingsCollectionWithMeterReadings).run {
                            databaseMeterReadings
                        }*/


                    val meterReadings = listOf<DatabaseMeterReading>(
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1000,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1010,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1030,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1060,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1070,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 1080,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        ),
                        DatabaseMeterReading(
                            parentMeterId = "1",
                            meterReading = 3000,
                            readingDate = DateUtils.formatDate(
                                "11/11/2021",
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: Date(),
                            parentMeterCollectionId = "1"
                        )
                    )


                    val machineOutput = MeterTariffMachine.processMeterReadings(
                        meterReadings,
                        meter.value?.meterType!!,
                        1
                    )
                    println(machineOutput.totalCost)
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    fun startMeterImageUpload(imageUri: Uri?) {
        if (imageUri != null) {
            showLoading.value = true
            viewModelScope.launch {
                authenticatedUser?.run {
                    storageManager.uploadImageURI(uid, meter.value!!.meterId, imageUri)
                }
                //TODO check why the progress bar doesn't show up
                showLoading.value = false

            }
        }
    }


}