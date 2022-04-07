package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.app.Application
import android.content.Intent
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersLocalDataSource
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import kotlinx.coroutines.launch
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.remote.FirebaseDatabaseManager
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.asDatabaseMeter
import com.mostapps.egyptianmeterstracker.screens.home.HomeActivity
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import org.koin.java.KoinJavaComponent


class MetersListViewModel(
    app: Application,
    private val localDataSource: MetersLocalDataSource
) : BaseViewModel(app) {

    val metersList = MutableLiveData<List<MeterDataListItem>>()
    private val firebaseDatabaseManager: FirebaseDatabaseManager
            by KoinJavaComponent.inject(FirebaseDatabaseManager::class.java)
    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by KoinJavaComponent.inject(FirebaseAuthenticationManager::class.java)

    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {
            val result = localDataSource.getMeters()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {
                    val dataList = ArrayList<MeterDataListItem>()
                    dataList.addAll((result.data as List<DatabaseMeter>).map { meter ->
                        MeterDataListItem(
                            name = meter.meterName,
                            lastRecordedReadingDate = DateUtils.formatDate(
                                meter.lastReadingDate,
                                DateUtils.DEFAULT_DATE_FORMAT
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


    val authenticationState: LiveData<Result<FirebaseUser>> =
        firebaseAuthenticationManager.getUserAuthenticationState()

    fun startDataSyncing(uid: String) {
        showLoading.value = true
        viewModelScope.launch {

            syncMetersData(uid)


            showLoading.postValue(false)
        }
    }

    private suspend fun syncMetersData(uid: String) {
        var remoteMeters: List<RemoteMeter> = emptyList()
        val remoteResult = firebaseDatabaseManager.getMetersByUserId(userId = uid)
        if (remoteResult is Result.Success<List<RemoteMeter>>) remoteMeters = remoteResult.data
        val databaseMeters = remoteMeters.asDatabaseMeter();
        localDataSource.bulkInsertMetersData(*(databaseMeters).toTypedArray())
        print("Inserted Meters")
        loadMeters()
    }


}