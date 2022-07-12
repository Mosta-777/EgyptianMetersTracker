package com.mostapps.egyptianmeterstracker.utils

import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.sortByOldestFirst
import com.mostapps.egyptianmeterstracker.enums.ElectricityMeterSubType
import com.mostapps.egyptianmeterstracker.enums.MeterSlice
import com.mostapps.egyptianmeterstracker.models.MeterReadingListItem
import com.mostapps.egyptianmeterstracker.models.MeterTariffMachineOutput
import kotlin.math.roundToInt

object ElectricityMeterTariffMachine {


    fun getReadingsListItems(
        meterReadings: List<DatabaseMeterReading>,
        meterSubType: Int
    ): MeterTariffMachineOutput {


        //Sort the list of not sorted

        val sortedMeterReadings = meterReadings.sortByOldestFirst()
        //Get Total consumption

        val totalConsumption =
            sortedMeterReadings.last().meterReading - sortedMeterReadings.first().meterReading

        //Determine the meter slice

        val currentSlice = getCurrentSlice(totalConsumption, meterSubType)
        val meterReadingListItem =
            getReadingsListItemsGivenCurrentSlice(
                meterSubType = meterSubType,
                currentSlice = currentSlice,
                meterReadings = sortedMeterReadings
            )
        val customerServiceFees = getCustomerServiceFeesGivenSlice(currentSlice, meterSubType)

        return MeterTariffMachineOutput(
            currentSlice = currentSlice,
            totalConsumption = totalConsumption,
            meterReadingsListItems = meterReadingListItem,
            meterReadingsCost = meterReadingListItem.sumOf { (it.priceFromLastReading).toDouble() },
            customerServiceFees = customerServiceFees,
            totalCost = meterReadingListItem.sumOf { (it.priceFromLastReading).toDouble() } + customerServiceFees
        )
    }


    private fun getReadingsListItemsGivenCurrentSlice(
        meterReadings: List<DatabaseMeterReading>,
        currentSlice: MeterSlice,
        meterSubType: Int
    ): List<MeterReadingListItem> {
        val limitsAndMultiplierList =
            getCurrentSliceLimitAndMultiplierList(currentSlice, meterSubType)
        val readingsList = mutableListOf<MeterReadingListItem>()
        for (index in meterReadings.indices) {
            if (index == 0) {
                readingsList.add(
                    MeterReadingListItem(
                        1,
                        meterReadings[index].meterReading.toString(),
                        "0", "0.0",
                        readingDate = DateUtils.formatDate(
                            meterReadings[index].readingDate,
                            DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                        ) ?: "-"
                    )
                )
            } else {
                val currentConsumption =
                    meterReadings[index].meterReading - meterReadings[index - 1].meterReading
                val currentTotalConsumption =
                    meterReadings[index].meterReading - meterReadings[0].meterReading

                val previousTotalConsumption =
                    meterReadings[index - 1].meterReading - meterReadings[0].meterReading
                val consumptionMultiplierPairs =
                    getConsumptionMultiplierPairs(
                        currentConsumption,
                        currentTotalConsumption,
                        previousTotalConsumption,
                        limitsAndMultiplierList
                    )

                val currentPrice = consumptionMultiplierPairs.getTotalCost()

                readingsList.add(
                    MeterReadingListItem(
                        index + 1,
                        meterReadings[index].meterReading.toString(),
                        currentConsumption.toString(),
                        ((currentPrice * 100.0).roundToInt() / 100.0).toString(),
                        readingDate = DateUtils.formatDate(
                            meterReadings[index].readingDate,
                            DateUtils.DEFAULT_DATE_FORMAT_WITHOUT_TIME
                        ) ?: "-"
                    )
                )
            }
        }
        return readingsList
    }

    private fun getConsumptionMultiplierPairs(
        currentConsumption: Int,
        currentTotalConsumption: Int,
        previousTotalConsumption: Int,
        limitsAndMultiplierList: MeterSliceLists
    ): List<ConsumptionMultiplierPair> {
        for (limitIndex in limitsAndMultiplierList.limitsList.indices) {
            if (currentTotalConsumption <= limitsAndMultiplierList.limitsList[limitIndex]) {
                //This means either this meter reading just passed a limit
                //or it simply didn't
                if (limitIndex == 0) {
                    //It surely didn't pass any limits as it's smaller than the smallest limit
                    //return it with the corresponding multiplier

                    //All of Slice 1, 3, 6, 7 readings

                    return listOf(
                        ConsumptionMultiplierPair(
                            currentConsumption,
                            limitsAndMultiplierList.multipliersList[limitIndex]
                        )
                    )
                } else if (limitIndex == 1) {
                    //It passed a single limit, if the previous cost was smaller than that limit
                    //part of the current consumption belongs to the previous multiplier

                    //The last readings of Slices 2, 4
                    if (previousTotalConsumption <= limitsAndMultiplierList.limitsList[0]) {
                        //It's a two part reading cost
                        return listOf(
                            ConsumptionMultiplierPair(
                                limitsAndMultiplierList.limitsList[0] - previousTotalConsumption,
                                limitsAndMultiplierList.multipliersList[0]
                            ),
                            ConsumptionMultiplierPair(
                                currentTotalConsumption - limitsAndMultiplierList.limitsList[0],
                                limitsAndMultiplierList.multipliersList[limitIndex]
                            )
                        )
                    } else {
                        //It's a one part reading cost
                        return listOf(
                            ConsumptionMultiplierPair(
                                currentConsumption,
                                limitsAndMultiplierList.multipliersList[limitIndex]
                            )
                        )
                    }
                } else if (limitIndex == 2) {

                    //The last readings of Slice 5

                    if (previousTotalConsumption <= limitsAndMultiplierList.limitsList[0]) {
                        //It's a three part reading cost
                        //A single reading jumped two limits!

                        return listOf(
                            ConsumptionMultiplierPair(
                                limitsAndMultiplierList.limitsList[0] - previousTotalConsumption,
                                limitsAndMultiplierList.multipliersList[0]
                            ),
                            ConsumptionMultiplierPair(
                                limitsAndMultiplierList.limitsList[1] - limitsAndMultiplierList.limitsList[0],
                                limitsAndMultiplierList.multipliersList[1]
                            ),
                            ConsumptionMultiplierPair(
                                currentTotalConsumption - limitsAndMultiplierList.limitsList[1],
                                limitsAndMultiplierList.multipliersList[limitIndex]
                            )
                        )


                    } else if (previousTotalConsumption <= limitsAndMultiplierList.limitsList[1]) {
                        //It's a two part reading cost
                        return listOf(
                            ConsumptionMultiplierPair(
                                limitsAndMultiplierList.limitsList[1] - previousTotalConsumption,
                                limitsAndMultiplierList.multipliersList[1]
                            ),
                            ConsumptionMultiplierPair(
                                currentTotalConsumption - limitsAndMultiplierList.limitsList[0],
                                limitsAndMultiplierList.multipliersList[limitIndex]
                            )
                        )
                    } else {
                        return listOf(
                            ConsumptionMultiplierPair(
                                currentConsumption,
                                limitsAndMultiplierList.multipliersList[limitIndex]
                            )
                        )
                    }
                }
            }
        }
        return listOf(
            ConsumptionMultiplierPair(
                currentConsumption,
                limitsAndMultiplierList.multipliersList.first()
            )
        )
    }

    private fun getCurrentMultiplier(
        currentTotalConsumption: Int,
        limitsAndMultiplierList: MeterSliceLists
    ): Double {
        for (limitIndex in limitsAndMultiplierList.limitsList.indices) {
            if (currentTotalConsumption <= limitsAndMultiplierList.limitsList[limitIndex]) {
                return limitsAndMultiplierList.multipliersList[limitIndex]
            }
        }
        return limitsAndMultiplierList.multipliersList.first()
    }


    private fun getCurrentSlice(totalConsumption: Int, meterSubtype: Int): MeterSlice {


        if (meterSubtype == ElectricityMeterSubType.HOME_USE.value) {
            return if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_ONE
            } else if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_SECOND_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_TWO
            } else if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_THREE
            } else if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_FOUR
            } else if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_FIFTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_FIVE
            } else if (totalConsumption <= MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_SIX
            } else if (totalConsumption > MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_SEVEN
            } else {
                MeterSlice.SLICE_ONE
            }
        } else {
            return if (totalConsumption <= MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIRST_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_ONE
            } else if (totalConsumption <= MeterTariffConstants.COMMERCIAL_ELECTRICITY_SECOND_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_TWO
            } else if (totalConsumption <= MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_THREE
            } else if (totalConsumption <= MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_FOUR
            } else if (totalConsumption > MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT) {
                MeterSlice.SLICE_FIVE
            } else {
                MeterSlice.SLICE_ONE
            }
        }
    }

    private fun getCurrentSliceLimitAndMultiplierList(
        meterSlice: MeterSlice,
        meterSubType: Int
    ): MeterSliceLists {
        return when (meterSlice) {
            MeterSlice.SLICE_ONE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterSliceLists(
                        limitsList = listOf(MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_KWH_LIMIT),
                        multipliersList = listOf(MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_MULTIPLIER)
                    )
                } else {
                    MeterSliceLists(
                        limitsList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIRST_SLICE_KWH_LIMIT),
                        multipliersList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIRST_SLICE_MULTIPLIER)
                    )
                }
            }
            MeterSlice.SLICE_TWO -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterSliceLists(
                        limitsList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_KWH_LIMIT,
                        ),
                        multipliersList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_MULTIPLIER,
                        )
                    )
                } else {
                    MeterSliceLists(
                        limitsList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_SECOND_SLICE_KWH_LIMIT),
                        multipliersList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_SECOND_SLICE_MULTIPLIER)
                    )
                }
            }
            MeterSlice.SLICE_THREE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterSliceLists(
                        limitsList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_KWH_LIMIT,
                        ),
                        multipliersList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_MULTIPLIER,
                        )
                    )
                } else {
                    MeterSliceLists(
                        limitsList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_KWH_LIMIT),
                        multipliersList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_MULTIPLIER)
                    )
                }
            }
            MeterSlice.SLICE_FOUR -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterSliceLists(
                        limitsList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_KWH_LIMIT,
                            MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT,
                        ),
                        multipliersList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_MULTIPLIER,
                            MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_MULTIPLIER,
                        )
                    )
                } else {
                    MeterSliceLists(
                        limitsList = listOf(
                            MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_KWH_LIMIT,
                            MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT,
                        ),
                        multipliersList = listOf(
                            MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_MULTIPLIER,
                            MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_MULTIPLIER,
                        )
                    )
                }
            }
            MeterSlice.SLICE_FIVE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterSliceLists(
                        limitsList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_KWH_LIMIT,
                            MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT,
                            MeterTariffConstants.HOME_ELECTRICITY_FIFTH_SLICE_KWH_LIMIT
                        ),
                        multipliersList = listOf(
                            MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_MULTIPLIER,
                            MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_MULTIPLIER,
                            MeterTariffConstants.HOME_ELECTRICITY_FIFTH_SLICE_MULTIPLIER,
                        )
                    )
                } else {
                    MeterSliceLists(
                        limitsList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_KWH_LIMIT),
                        multipliersList = listOf(MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIFTH_SLICE_MULTIPLIER)
                    )
                }
            }
            MeterSlice.SLICE_SIX -> {
                MeterSliceLists(
                    limitsList = listOf(
                        MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_KWH_LIMIT,
                    ),
                    multipliersList = listOf(
                        MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_MULTIPLIER,
                    )
                )
            }
            MeterSlice.SLICE_SEVEN -> {
                MeterSliceLists(
                    limitsList = listOf(
                        MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_KWH_LIMIT,
                    ),
                    multipliersList = listOf(
                        MeterTariffConstants.HOME_ELECTRICITY_SEVENTH_SLICE_MULTIPLIER,
                    )
                )
            }
        }
    }


    private fun getCustomerServiceFeesGivenSlice(meterSlice: MeterSlice, meterSubType: Int): Int {
        return when (meterSlice) {
            MeterSlice.SLICE_ONE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterTariffConstants.HOME_ELECTRICITY_FIRST_SLICE_CUSTOMER_SERVICE_FEE
                } else {
                    MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIRST_SLICE_CUSTOMER_SERVICE_FEE
                }
            }
            MeterSlice.SLICE_TWO -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterTariffConstants.HOME_ELECTRICITY_SECOND_SLICE_CUSTOMER_SERVICE_FEE
                } else {
                    MeterTariffConstants.COMMERCIAL_ELECTRICITY_SECOND_SLICE_CUSTOMER_SERVICE_FEE
                }
            }
            MeterSlice.SLICE_THREE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterTariffConstants.HOME_ELECTRICITY_THIRD_SLICE_CUSTOMER_SERVICE_FEE
                } else {
                    MeterTariffConstants.COMMERCIAL_ELECTRICITY_THIRD_SLICE_CUSTOMER_SERVICE_FEE
                }
            }
            MeterSlice.SLICE_FOUR -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterTariffConstants.HOME_ELECTRICITY_FOURTH_SLICE_CUSTOMER_SERVICE_FEE
                } else {
                    MeterTariffConstants.COMMERCIAL_ELECTRICITY_FOURTH_SLICE_CUSTOMER_SERVICE_FEE
                }
            }
            MeterSlice.SLICE_FIVE -> {
                if (meterSubType == ElectricityMeterSubType.HOME_USE.value) {
                    MeterTariffConstants.HOME_ELECTRICITY_FIFTH_SLICE_CUSTOMER_SERVICE_FEE
                } else {
                    MeterTariffConstants.COMMERCIAL_ELECTRICITY_FIFTH_SLICE_CUSTOMER_SERVICE_FEE
                }
            }
            MeterSlice.SLICE_SIX -> {
                MeterTariffConstants.HOME_ELECTRICITY_SIXTH_SLICE_CUSTOMER_SERVICE_FEE
            }
            MeterSlice.SLICE_SEVEN -> {
                MeterTariffConstants.HOME_ELECTRICITY_SEVENTH_SLICE_CUSTOMER_SERVICE_FEE
            }
        }
    }


}

data class MeterSliceLists(val limitsList: List<Int>, val multipliersList: List<Double>)
data class ConsumptionMultiplierPair(val consumption: Int, val multiplier: Double)

fun List<ConsumptionMultiplierPair>.getTotalCost(): Double {
    return this.sumOf { it.consumption * it.multiplier }
}