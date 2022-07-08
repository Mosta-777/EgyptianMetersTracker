package com.mostapps.egyptianmeterstracker.data.local.entites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mostapps.egyptianmeterstracker.data.remote.models.RemoteMeterReading
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import java.util.*


@Entity(tableName = "meterReadings")
data class DatabaseMeterReading(
    @PrimaryKey @ColumnInfo(name = "meterReadingId") val meterReadingId: String = UUID.randomUUID()
        .toString(),
    @ColumnInfo(name = "parentMeterId") val parentMeterId: String,
    @ColumnInfo(name = "parentMeterCollectionId") val parentMeterCollectionId: String,
    @ColumnInfo(name = "meterReading") var meterReading: Int,
    @ColumnInfo(name = "readingDate") var readingDate: Date
)


fun List<DatabaseMeterReading>.asRemoteMeterReading(): List<RemoteMeterReading> {
    return map {
        RemoteMeterReading(
            meterReadingId = it.meterReadingId,
            parentMeterId = it.parentMeterId,
            parentMeterCollectionId = it.parentMeterCollectionId,
            meterReading = it.meterReading,
            readingDate = DateUtils.formatDate(
                it.readingDate,
                DateUtils.DEFAULT_DATE_FORMAT
            )
        )
    }
}
