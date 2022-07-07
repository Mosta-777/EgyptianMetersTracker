package com.mostapps.egyptianmeterstracker.data.remote.models

import com.google.firebase.database.IgnoreExtraProperties
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.utils.DateUtils


@IgnoreExtraProperties
class RemoteMeterReading(
    val meterReadingId: String? = null,
    val parentMeterId: String? = null,
    val parentMeterCollectionId: String? = null,
    var meterReading: Int? = null,
    var readingDate: String? = null
)


fun List<RemoteMeterReading>.asDatabaseMeterReading(): List<DatabaseMeterReading> {
    return map {
        DatabaseMeterReading(
            meterReadingId = it.meterReadingId!!,
            parentMeterId = it.parentMeterId!!,
            parentMeterCollectionId = it.parentMeterCollectionId!!,
            meterReading = it.meterReading!!,
            readingDate = DateUtils.formatDate(
                it.readingDate,
                DateUtils.DEFAULT_DATE_FORMAT
            )
        )
    }
}