package com.mostapps.egyptianmeterstracker.data.dto.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "meterReadings")
data class MeterReadingDTO(
    @PrimaryKey @ColumnInfo(name = "meterReadingId") val meterReadingId: String = UUID.randomUUID()
        .toString(),
    @ColumnInfo(name = "parentMeterId") var parentMeterId: String?,
    @ColumnInfo(name = "meterReading") var meterReading: Int?,
    @ColumnInfo(name = "readingDate") var readingDate: Date?
)
