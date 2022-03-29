package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mostapps.egyptianmeterstracker.data.Converters
import com.mostapps.egyptianmeterstracker.data.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.entites.MeterReadingsCollectionDTO


@Database(
    entities = [MeterDTO::class, MeterReadingsCollectionDTO::class, MeterReadingDTO::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MetersDatabase : RoomDatabase() {
    abstract fun meterDao(): MetersDao
}