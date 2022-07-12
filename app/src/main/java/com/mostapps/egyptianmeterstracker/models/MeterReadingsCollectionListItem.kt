package com.mostapps.egyptianmeterstracker.models

data class MeterReadingsCollectionListItem(
    var collectionId: String,
    var startDate: String,
    var endDate: String,
    var currentMeterSlice: String,
    var totalCost: String,
    var totalConsumption: String,
    var nestedMeterReadingsListItems: List<MeterReadingListItem>,
    var isExpanded: Boolean = false
)