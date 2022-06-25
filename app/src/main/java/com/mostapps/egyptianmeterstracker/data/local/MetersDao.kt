package com.mostapps.egyptianmeterstracker.data.local

import androidx.room.*
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterReadingsCollectionWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadingsCollections


@Dao
interface MetersDao {


    //Meters specific queries
    @Query("SELECT * FROM meters")
    suspend fun getAllMeters(): List<DatabaseMeter>

    @Query("SELECT * FROM meters where meterId = :meterId")
    suspend fun getMeterById(meterId: String): DatabaseMeter?

    @Update
    suspend fun updateMeter(databaseMeter: DatabaseMeter)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeter(databaseMeter: DatabaseMeter)

    //On conflict with remote database don't replace data, The local database has the priority
    //and will overwrite the data on the server
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllMeters(vararg databaseMeters: DatabaseMeter)

    @Query("DELETE from meters WHERE meterId = :meterId")
    suspend fun deleteMeter(meterId: String)

    //////////////////////////////////////////////////////////////////////////////////

    //Meter Readings specific queries
    @Query("SELECT * FROM meterReadings")
    suspend fun getAllMeterReadings(): List<DatabaseMeterReading>

    @Query("SELECT * FROM meterReadings where meterReadingId = :meterReadingId")
    suspend fun getMeterReadingById(meterReadingId: String): DatabaseMeterReading?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeterReading(databaseMeterReading: DatabaseMeterReading)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun bulkInsertMeterReading(vararg databaseMeterReadings: DatabaseMeterReading)


    //////////////////////////////////////////////////////////////////////////////////

    //Meter Readings Collections specific queries
    @Query("SELECT * FROM meterReadingsCollection")
    suspend fun getAllMeterReadingsCollections(): List<DatabaseMeterReadingsCollection>

    @Query("SELECT * FROM meterReadingsCollection WHERE isFinished")
    suspend fun getFinishedMeterReadingsCollections(): List<DatabaseMeterReadingsCollection>


    @Query("SELECT * FROM meterReadingsCollection where meterReadingsCollectionId = :meterReadingsCollectionId")
    suspend fun getMeterReadingsCollectionById(meterReadingsCollectionId: String): DatabaseMeterReadingsCollection?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeterReadingsCollection(meterReadingsCollectionDTO: DatabaseMeterReadingsCollection)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun bulkInsertMeterReadingsCollections(vararg meterReadingsCollections: DatabaseMeterReadingsCollection)

    /////////////////////////////////////////////////////////////////////////////////
    //Relations queries
    @Transaction
    @Query("SELECT * FROM meters WHERE meterId = :meterId")
    suspend fun getMeterWithMeterReadings(meterId: String): MeterWithMeterReadings


    @Transaction
    @Query("SELECT * FROM meters WHERE meterId = :meterId")
    suspend fun getMeterWithMeterReadingsCollections(meterId: String): MeterWithMeterReadingsCollections


    @Transaction
    @Query("SELECT * FROM meterReadingsCollection WHERE meterReadingsCollectionId = :meterReadingsCollectionId")
    suspend fun getMeterReadingsCollectionWithMeterReadings(meterReadingsCollectionId: String): MeterReadingsCollectionWithMeterReadings


    @Transaction
    suspend fun saveMeter(
        databaseMeter: DatabaseMeter,
        meterReadingsCollection: DatabaseMeterReadingsCollection,
        firstDatabaseMeterReading: DatabaseMeterReading,
        currentDatabaseMeterReading: DatabaseMeterReading
    ) {
        insertMeter(databaseMeter)
        insertMeterReadingsCollection(meterReadingsCollection)
        insertMeterReading(firstDatabaseMeterReading)
        insertMeterReading(currentDatabaseMeterReading)
    }

}