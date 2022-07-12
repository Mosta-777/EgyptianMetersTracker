package com.mostapps.egyptianmeterstracker.enums

import com.mostapps.egyptianmeterstracker.MyApp
import com.mostapps.egyptianmeterstracker.R
import org.koin.dsl.koinApplication

enum class MeterSlice(val meterSliceValue: Int) {
    SLICE_ONE(1),
    SLICE_TWO(2),
    SLICE_THREE(3),
    SLICE_FOUR(4),
    SLICE_FIVE(5),
    SLICE_SIX(6),
    SLICE_SEVEN(7);


    companion object {
        fun getSliceStringFromSliceValue(sliceValue: Int): String {

            return when (sliceValue) {
                SLICE_ONE.meterSliceValue -> MyApp.Strings.get(R.string.slice_one)
                SLICE_TWO.meterSliceValue -> MyApp.Strings.get(R.string.slice_two)
                SLICE_THREE.meterSliceValue -> MyApp.Strings.get(R.string.slice_three)
                SLICE_FOUR.meterSliceValue -> MyApp.Strings.get(R.string.slice_four)
                SLICE_FIVE.meterSliceValue -> MyApp.Strings.get(R.string.slice_five)
                SLICE_SIX.meterSliceValue -> MyApp.Strings.get(R.string.slice_six)
                SLICE_SEVEN.meterSliceValue -> MyApp.Strings.get(R.string.slice_seven)
                else -> MyApp.Strings.get(R.string.slice_one)

            }


        }

    }

}