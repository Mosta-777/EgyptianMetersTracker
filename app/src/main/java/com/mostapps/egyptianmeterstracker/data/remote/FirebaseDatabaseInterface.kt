package com.mostapps.egyptianmeterstracker.data.remote


import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result

interface FirebaseDatabaseInterface {


    fun saveUser(user: User, userId: String)

    suspend fun getMetersByUserId(
        userId: String
    ): Result<List<RemoteMeter>>

    suspend fun getMeterCollectionsByUserId(
        userId: String
    ): Result<List<DatabaseMeterReadingsCollection>>

    fun getMeterReadingsByUserIdAndMeterCollectionId(
        userId: String,
        meterCollectionId: String,
        onResult: (Result<List<DatabaseMeterReading>>) -> Unit
    )
}