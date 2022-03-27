package com.mostapps.egyptianmeterstracker.models

import java.io.Serializable

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class MeterDataListItem(
    var name: String?,
    var lastRecordedReadingDate: String?,
    var meterType: Int?

) : Serializable