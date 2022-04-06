package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReadingsCollection

class MeterReadingsCollectionWithMeterReadings(
    @Embedded val meterCollection: DatabaseMeterReadingsCollection,
    @Relation(
        parentColumn = "meterReadingsCollectionId",
        entityColumn = "parentMeterCollectionId"
    )
    val databaseMeterReadings: List<DatabaseMeterReading>
)