package com.mostapps.egyptianmeterstracker.data.remote.models

import com.google.firebase.database.IgnoreExtraProperties
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import java.util.*


@IgnoreExtraProperties
data class RemoteMeterReadingsCollection(
    val meterReadingsCollectionId: String? = null,
    val parentMeterId: String? = null,
    val collectionStartDate: String? = null,
    val collectionEndDate: String? = null,
    var collectionCurrentSlice: Int? = null,
    var isFinished: Boolean? = false
)


fun List<RemoteMeterReadingsCollection>.asDatabaseMeterCollection(): List<DatabaseMeterReadingsCollection> {
    return map {
        DatabaseMeterReadingsCollection(
            meterReadingsCollectionId = it.meterReadingsCollectionId!!,
            parentMeterId = it.parentMeterId!!,
            collectionStartDate = DateUtils.formatDate(
                it.collectionStartDate,
                DateUtils.DEFAULT_DATE_FORMAT
            )?: Date(),
            collectionEndDate = DateUtils.formatDate(
                it.collectionEndDate,
                DateUtils.DEFAULT_DATE_FORMAT
            ),
            collectionCurrentSlice = it.collectionCurrentSlice!!,
            isFinished = it.isFinished
        )
    }
}