package com.mostapps.egyptianmeterstracker.data.local

import android.content.Context
import androidx.room.Room


object LocalDB {

    fun createMetersDao(context: Context): MetersDao {
        return Room.databaseBuilder(
            context.applicationContext,
            MetersDatabase::class.java, "egyptianMetersTracker.db"
        ).build().meterDao()
    }

}