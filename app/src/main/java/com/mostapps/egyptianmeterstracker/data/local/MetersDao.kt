package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.*
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.relations.MeterWithMeterReadings


@Dao
interface MetersDao {


    @Query("SELECT * FROM meters")
    suspend fun getMeters(): List<MeterDTO>

    @Query("SELECT * FROM meters where meterId = :meterId")
    suspend fun getMeterById(meterId: String): MeterDTO?

    @Update
    suspend fun updateMeter(meter: MeterDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMeter(meter: MeterDTO)

    @Query("SELECT * FROM meterReadings")
    suspend fun getMeterReadings(): List<MeterReadingDTO>

    @Query("SELECT * FROM meterReadings where meterReadingId = :meterReadingId")
    suspend fun getMeterReadingById(meterReadingId: String): MeterReadingDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReading(meterReadingDTO: MeterReadingDTO)

    @Transaction
    @Query("SELECT * FROM meters WHERE meterId = :meterId")
    suspend fun getMeterWithMeterReadings(meterId: String): List<MeterWithMeterReadings>

}