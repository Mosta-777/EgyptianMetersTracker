package com.mostapps.egyptianmeterstracker.data.remote.models

import com.google.firebase.database.IgnoreExtraProperties
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.utils.DateUtils

@IgnoreExtraProperties
data class RemoteMeter(
    val meterId: String? = null,
    var meterName: String? = null,
    val meterType: Int? = null,
    val meterSubType: Int? = null,
    var lastReadingDate: String? = null
)


fun List<RemoteMeter>.asDatabaseMeter(): List<DatabaseMeter> {
    return map {
        DatabaseMeter(
            meterId = it.meterId!!,
            meterName = it.meterName!!,
            meterType = it.meterType!!,
            meterSubType = it.meterSubType!!,
            lastReadingDate = DateUtils.formatDate(
                it.lastReadingDate,
                DateUtils.DEFAULT_DATE_FORMAT
            )
        )
    }
}