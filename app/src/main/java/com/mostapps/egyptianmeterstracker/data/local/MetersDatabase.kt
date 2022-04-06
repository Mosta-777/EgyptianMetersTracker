package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mostapps.egyptianmeterstracker.data.Converters
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection


@Database(
    entities = [DatabaseMeter::class, DatabaseMeterReadingsCollection::class, DatabaseMeterReading::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MetersDatabase : RoomDatabase() {
    abstract fun meterDao(): MetersDao
}