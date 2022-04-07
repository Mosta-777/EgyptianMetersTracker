package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import kotlinx.coroutines.launch
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import org.koin.java.KoinJavaComponent


class MetersListViewModel(
    app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {

    val metersList = MutableLiveData<List<MeterDataListItem>>()

    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by KoinJavaComponent.inject(FirebaseAuthenticationManager::class.java)

    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getMeters()
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


    val authenticatedUser: FirebaseUser? =
        firebaseAuthenticationManager.getCurrentUser()

    fun startDataSyncing(uid: String) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.syncAllData(uid)
            showLoading.postValue(false)
            loadMeters()
        }
    }


}