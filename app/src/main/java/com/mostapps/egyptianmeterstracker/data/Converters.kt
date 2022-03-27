package com.mostapps.egyptianmeterstracker.data

import androidx.room.TypeConverter
import com.mostapps.egyptianmeterstracker.utils.DateUtils
import java.util.*

class Converters {


    @TypeConverter
    fun fromStringToDate(dateStringValue: String?): Date? {
        return dateStringValue?.let {
            DateUtils.formatDate(
                dateStringValue,
                DateUtils.DEFAULT_FORMAT_DATE
            )
        }
    }

    @TypeConverter
    fun dateToFormattedString(date: Date?): String? {
        return date?.let {
            DateUtils.formatDate(
                date,
                DateUtils.DEFAULT_FORMAT_DATE
            )
        }
    }

}