package com.mostapps.egyptianmeterstracker.data.dto.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "meters")
data class MeterDTO(
    @PrimaryKey @ColumnInfo(name = "meterId") val meterId: String,
    @ColumnInfo(name = "meterName") var meterName: String?,
    @ColumnInfo(name = "meterType") val meterType: Int?,
    @ColumnInfo(name = "lastReadingDate") var lastReadingDate: Date?
)
