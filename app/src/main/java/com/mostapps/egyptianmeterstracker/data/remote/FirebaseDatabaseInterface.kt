package com.mostapps.egyptianmeterstracker.data.remote


import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.models.User
import com.mostapps.egyptianmeterstracker.utils.Result

interface FirebaseDatabaseInterface {


    fun saveUser(user: User, userId: String)

    fun getMetersByUserId(userId: String, onResult: (Result<List<DatabaseMeter>>) -> Unit)
    fun getMeterCollectionsByUserId(
        userId: String,
        onResult: (Result<List<DatabaseMeterReadingsCollection>>) -> Unit
    )

    fun getMeterReadingsByUserIdAndMeterCollectionId(
        userId: String,
        meterCollectionId: String,
        onResult: (Result<List<DatabaseMeterReading>>) -> Unit
    )
}