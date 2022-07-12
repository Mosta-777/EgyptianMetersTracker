package com.mostapps.egyptianmeterstracker.models

data class MeterReadingListItem(
    var readingNumber: Int,
    var readingValue: String,
    var consumptionFromLastReading: String,
    var priceFromLastReading: String,
    var readingDate: String
)
