package com.mostapps.egyptianmeterstracker.data.remote.models

import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.utils.DateUtils


class RemoteMeterReadingsCollection(
    val meterReadingsCollectionId: String?,
    val parentMeterId: String?,
    val collectionStartDate: String?,
    val collectionEndDate: String? = null,
    var collectionCurrentSlice: Int?,
    var isFinished: Boolean? = false
)


fun List<RemoteMeterReadingsCollection>.asDatabaseMeter(): List<DatabaseMeterReadingsCollection> {
    return map {
        DatabaseMeterReadingsCollection(
            meterReadingsCollectionId = it.meterReadingsCollectionId!!,
            parentMeterId = it.parentMeterId,
            collectionStartDate = DateUtils.formatDate(
                it.collectionStartDate,
                DateUtils.DEFAULT_DATE_FORMAT
            ),
            collectionEndDate = DateUtils.formatDate(
                it.collectionEndDate,
                DateUtils.DEFAULT_DATE_FORMAT
            ),
            collectionCurrentSlice = it.collectionCurrentSlice,
            isFinished = it.isFinished
        )
    }
}