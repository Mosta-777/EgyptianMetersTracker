package com.mostapps.egyptianmeterstracker.data.remote


import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReading
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.remote.models.User
import com.mostapps.egyptianmeterstracker.utils.Result

interface FirebaseDatabaseInterface {


    //User related functions
    suspend fun saveUser(user: User, userId: String)

    //Meter data related function
    suspend fun downloadMetersOfUserId(
        userId: String
    ): Result<List<RemoteMeter>>

    suspend fun uploadMetersOfUserId(userId: String, metersToUpload: Map<String, RemoteMeter>)


    //Meter reading collections related functions
    suspend fun downloadMeterCollectionsOfUserIdAndMeterId(
        userId: String,
        meterId: String
    ): Result<List<RemoteMeterReadingsCollection>>

    suspend fun uploadMeterCollectionsOfUserIdAndMeterId(
        userId: String,
        meterId: String,
        meterReadingCollectionsToUpload: Map<String, RemoteMeterReadingsCollection>
    )


    //Meter readings related functions
    suspend fun downloadMeterReadingsOfUserIdAndMeterId(
        userId: String,
        meterId: String
    ): Result<List<RemoteMeterReading>>

    suspend fun uploadMeterReadingsOfUserIdAndMeterId(
        userId: String,
        meterId: String,
        meterReadingsToUpload: Map<String, RemoteMeterReading>
    )

}