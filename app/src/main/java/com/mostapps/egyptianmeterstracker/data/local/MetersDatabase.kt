package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mostapps.egyptianmeterstracker.data.Converters
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO


@Database(entities = [MeterDTO::class, MeterReadingDTO::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MetersDatabase : RoomDatabase() {
    abstract fun meterDao(): MetersDao
}