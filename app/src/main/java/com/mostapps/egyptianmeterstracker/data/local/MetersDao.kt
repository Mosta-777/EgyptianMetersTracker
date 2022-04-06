package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.*
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterReadingsCollectionWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections


@Dao
interface MetersDao {


    //Meters specific queries
    @Query("SELECT * FROM meters")
    suspend fun getAllMeters(): List<MeterDTO>

    @Query("SELECT * FROM meters where meterId = :meterId")
    suspend fun getMeterById(meterId: String): MeterDTO?

    @Update
    suspend fun updateMeter(meter: MeterDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeter(meter: MeterDTO)

    //////////////////////////////////////////////////////////////////////////////////

    //Meter Readings specific queries
    @Query("SELECT * FROM meterReadings")
    suspend fun getAllMeterReadings(): List<MeterReadingDTO>

    @Query("SELECT * FROM meterReadings where meterReadingId = :meterReadingId")
    suspend fun getMeterReadingById(meterReadingId: String): MeterReadingDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReading(meterReadingDTO: MeterReadingDTO)


    //////////////////////////////////////////////////////////////////////////////////

    //Meter Readings Collections specific queries
    @Query("SELECT * FROM meterReadingsCollection")
    suspend fun getAllMeterReadingsCollections(): List<MeterReadingsCollectionDTO>

    @Query("SELECT * FROM meterReadingsCollection WHERE isFinished")
    suspend fun getFinishedMeterReadingsCollections(): List<MeterReadingsCollectionDTO>


    @Query("SELECT * FROM meterReadingsCollection where meterReadingsCollectionId = :meterReadingsCollectionId")
    suspend fun getMeterReadingsCollectionById(meterReadingsCollectionId: String): MeterReadingsCollectionDTO?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeterReadingsCollection(meterReadingsCollectionDTO: MeterReadingsCollectionDTO)

    /////////////////////////////////////////////////////////////////////////////////
    //Relations queries
    @Transaction
    @Query("SELECT * FROM meters WHERE meterId = :meterId")
    suspend fun getMeterWithMeterReadings(meterId: String): List<MeterWithMeterReadings>


    @Transaction
    @Query("SELECT * FROM meters WHERE meterId = :meterId")
    suspend fun getMeterWithMeterReadingsCollections(meterId: String): List<MeterWithMeterReadingsCollections>


    @Transaction
    @Query("SELECT * FROM meterReadingsCollection WHERE meterReadingsCollectionId = :meterReadingsCollectionId")
    suspend fun getMeterReadingsCollectionWithMeterReadings(meterReadingsCollectionId: String): List<MeterReadingsCollectionWithMeterReadings>


    @Transaction
    suspend fun saveMeter(
        meter: MeterDTO,
        meterReadingsCollection: MeterReadingsCollectionDTO,
        firstMeterReading: MeterReadingDTO,
        currentMeterReading: MeterReadingDTO
    ) {
        insertMeter(meter)
        insertMeterReadingsCollection(meterReadingsCollection)
        insertMeterReading(firstMeterReading)
        insertMeterReading(currentMeterReading)
    }

}