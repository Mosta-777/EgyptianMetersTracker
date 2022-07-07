package com.mostapps.egyptianmeterstracker.models

data class MeterReadingListItem(
    var readingNumber: Int,
    var readingValue: Int,
    var consumptionFromLastReading: Int,
    var priceFromLastReading: Double
)
