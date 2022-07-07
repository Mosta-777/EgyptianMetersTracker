package com.mostapps.egyptianmeterstracker.models

import com.mostapps.egyptianmeterstracker.enums.MeterSlice

data class MeterTariffMachineOutput(
    val currentSlice: MeterSlice,
    val totalConsumption: Int,
    val totalCost: Double,
    val meterReadingsListItems: List<MeterReadingListItem>,
    val meterReadingsCost: Double,
    val customerServiceFees: Int,
)