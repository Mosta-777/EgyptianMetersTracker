package com.mostapps.egyptianmeterstracker.meterslist

import java.io.Serializable

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class MeterDataListItem(
    var name: String?,
    var lastRecordedReading: String?

) : Serializable