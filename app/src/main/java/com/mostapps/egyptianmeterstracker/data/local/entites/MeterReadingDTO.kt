package com.mostapps.egyptianmeterstracker.data.local.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "meterReadings")
data class MeterReadingDTO(
    @PrimaryKey @ColumnInfo(name = "meterReadingId") val meterReadingId: String = UUID.randomUUID()
        .toString(),
    @ColumnInfo(name = "parentMeterId") val parentMeterId: String?,
    @ColumnInfo(name = "parentMeterCollectionId") val parentMeterCollectionId: String?,
    @ColumnInfo(name = "meterReading") var meterReading: Int?,
    @ColumnInfo(name = "readingDate") var readingDate: Date?
)
