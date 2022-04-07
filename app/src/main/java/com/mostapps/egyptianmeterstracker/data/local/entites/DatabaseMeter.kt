package com.mostapps.egyptianmeterstracker.data.local.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeter
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import java.util.*


@Entity(tableName = "meters")
data class DatabaseMeter(
    @PrimaryKey @ColumnInfo(name = "meterId") val meterId: String,
    @ColumnInfo(name = "meterName") var meterName: String?,
    @ColumnInfo(name = "meterType") val meterType: Int?,
    @ColumnInfo(name = "meterSubType") val meterSubType: Int?,
    @ColumnInfo(name = "lastReadingDate") var lastReadingDate: Date?
)

fun List<DatabaseMeter>.asRemoteModel(): List<RemoteMeter> {
    return map {
        RemoteMeter(
            meterId = it.meterId,
            meterName = it.meterName,
            meterType = it.meterType,
            meterSubType = it.meterSubType,
            lastReadingDate = DateUtils.formatDate(
                it.lastReadingDate,
                DateUtils.DEFAULT_DATE_FORMAT
            )
        )
    }
}
