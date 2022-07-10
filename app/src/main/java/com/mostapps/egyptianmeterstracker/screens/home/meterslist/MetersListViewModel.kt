package com.mostapps.egyptianmeterstracker.screens.home.meterslist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.mostapps.egyptianmeterstracker.R
import com.mostapps.egyptianmeterstracker.authentication.FirebaseAuthenticationManager
import com.mostapps.egyptianmeterstracker.base.BaseViewModel
import com.mostapps.egyptianmeterstracker.data.local.MetersDataSource
import com.mostapps.egyptianmeterstracker.models.MeterDataListItem
import kotlinx.coroutines.launch
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.sortByNewestFirst
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import org.koin.java.KoinJavaComponent


class MetersListViewModel(
    val app: Application,
    private val dataSource: MetersDataSource
) : BaseViewModel(app) {


    var metersList = emptyList<DatabaseMeter>()
    val metersListItems = MutableLiveData<List<MeterDataListItem>>()


    val showResolveConflictDialogue = MutableLiveData<Int>()

    private val firebaseAuthenticationManager: FirebaseAuthenticationManager
            by KoinJavaComponent.inject(FirebaseAuthenticationManager::class.java)


    val authenticatedUser: FirebaseUser? =
        firebaseAuthenticationManager.getCurrentUser()


    fun loadMeters() {
        showLoading.value = true
        viewModelScope.launch {
            val result = dataSource.getMeters()
            showLoading.postValue(false)
            when (result) {
                is Result.Success<*> -> {

                    metersList = (result.data as List<DatabaseMeter>).sortByNewestFirst()
                    val dataList = ArrayList<MeterDataListItem>()
                    dataList.addAll((metersList).map { meter ->
                        MeterDataListItem(
                            name = meter.meterName,
                            lastRecordedReadingDate = DateUtils.formatDate(
                                meter.lastReadingDate,
                                DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                            ) ?: "-",
                            meterType = meter.meterType
                        )
                    })
                    metersListItems.value = dataList
                }
                is Result.Error ->
                    showSnackBar.value = result.message
            }
            //check if no data has to be shown
            invalidateShowNoData()
        }
    }

    private fun invalidateShowNoData() {
        showNoData.value = metersListItems.value == null || metersListItems.value!!.isEmpty()
    }


    fun startDataSyncing(uid: String) {
        showLoading.value = true
        viewModelScope.launch {
            val noOfConflicts = dataSource.syncNonConflictedData(uid)
            showLoading.postValue(false)
            loadMeters()
            showToast.postValue(app.getString(R.string.non_conflicted_data_synced))
            showResolveConflictDialogue.postValue(noOfConflicts)
        }
    }

    fun syncConflictedData(numberOfConflicts: Int, keepLocal: Boolean, uid: String) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.syncConflictedData(uid = uid, keepLocalData = keepLocal)
            showLoading.postValue(false)
            loadMeters()
            showToast.postValue(app.getString(R.string.conflicted_data_synced))
        }
    }


}