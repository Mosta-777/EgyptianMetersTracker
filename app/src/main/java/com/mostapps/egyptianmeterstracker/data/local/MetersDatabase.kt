package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO


@Database(entities = [MeterDTO::class, MeterReadingDTO::class], version = 1, exportSchema = false)
abstract class MetersDatabase : RoomDatabase() {
    abstract fun meterDao(): MetersDao
}