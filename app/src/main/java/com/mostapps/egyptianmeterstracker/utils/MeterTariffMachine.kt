package com.mostapps.egyptianmeterstracker.utils

import com.mostapps.egyptianmeterstracker.enums.MeterTypes
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.models.MeterTariffMachineOutput

object MeterTariffMachine {


    //TODO arrange meter reading in ascending order in the database


    fun processMeterReadings(
        meterReadings: List<DatabaseMeterReading>,
        meterType: Int,
        meterSubType: Int
    ): MeterTariffMachineOutput {

        //For now the app works only for Electricity meters

        return when (meterType) {
            MeterTypes.ELECTRICITY.meterValue -> ElectricityMeterTariffMachine.getReadingsListItems(
                meterReadings, meterSubType
            )
            else -> ElectricityMeterTariffMachine.getReadingsListItems(meterReadings, meterSubType)
        }
    }


}