package com.mostapps.egyptianmeterstracker.data.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "meterReadingsCollection")
class MeterReadingsCollectionDTO(
    @PrimaryKey @ColumnInfo(name = "meterReadingsCollectionId") val meterReadingsCollectionId: String,
    @ColumnInfo(name = "parentMeterId") val parentMeterId: String?,
    @ColumnInfo(name = "collectionStartDate") val collectionStartDate: Date?,
    @ColumnInfo(name = "collectionEndDate") val collectionEndDate: Date? = null,
    @ColumnInfo(name = "collectionCurrentSlice") var collectionCurrentSlice: Int?,
    @ColumnInfo(name = "isFinished") var isFinished: Boolean? = false
)