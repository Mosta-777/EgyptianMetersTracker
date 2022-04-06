package com.mostapps.egyptianmeterstracker.data.remote


import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.utils.Result

interface FirebaseDatabaseInterface {


    fun getMetersByUserId(userId: String, onResult: (Result<List<MeterDTO>>) -> Unit)
    fun getMeterCollectionsByUserId(
        userId: String,
        onResult: (Result<List<MeterReadingsCollectionDTO>>) -> Unit
    )

    fun getMeterReadingsByUserIdAndMeterCollectionId(
        userId: String,
        meterCollectionId: String,
        onResult: (Result<List<MeterReadingDTO>>) -> Unit
    )
}