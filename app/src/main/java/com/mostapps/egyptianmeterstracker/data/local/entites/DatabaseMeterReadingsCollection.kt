package com.mostapps.egyptianmeterstracker.data.local.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import java.util.*


@Entity(tableName = "meterReadingsCollection")
class DatabaseMeterReadingsCollection(
    @PrimaryKey @ColumnInfo(name = "meterReadingsCollectionId") val meterReadingsCollectionId: String,
    @ColumnInfo(name = "parentMeterId") val parentMeterId: String,
    @ColumnInfo(name = "collectionStartDate") val collectionStartDate: Date,
    @ColumnInfo(name = "collectionEndDate") val collectionEndDate: Date? = null,
    @ColumnInfo(name = "collectionCurrentSlice") var collectionCurrentSlice: Int,
    @ColumnInfo(name = "totalConsumption") var totalConsumption: Int,
    @ColumnInfo(name = "totalCost") var totalCost: Double,
    @ColumnInfo(name = "isFinished") var isFinished: Boolean? = false
)


@Entity
class DatabaseMeterReadingsCollectionMainDataUpdate(
    @ColumnInfo(name = "meterReadingsCollectionId") val meterReadingsCollectionId: String,
    @ColumnInfo(name = "collectionCurrentSlice") var collectionCurrentSlice: Int,
    @ColumnInfo(name = "totalConsumption") var totalConsumption: Int,
    @ColumnInfo(name = "totalCost") var totalCost: Double,
)


fun List<DatabaseMeterReadingsCollection>.sortByNewestFirst(): List<DatabaseMeterReadingsCollection> {
    return sortedByDescending { it.collectionStartDate }
}


fun List<DatabaseMeterReadingsCollection>.asRemoteMeterReadingsCollection(): List<RemoteMeterReadingsCollection> {
    return map {
        RemoteMeterReadingsCollection(
            meterReadingsCollectionId = it.meterReadingsCollectionId,
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
            totalConsumption = it.totalConsumption,
            totalCost = it.totalCost,
            isFinished = it.isFinished
        )
    }
}