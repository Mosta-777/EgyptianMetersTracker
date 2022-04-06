package com.mostapps.egyptianmeterstracker.data.local.entites.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeter
import com.mostapps.egyptianmeterstracker.data.local.entites.DatabaseMeterReading


data class MeterWithMeterReadings(
    @Embedded val databaseMeter: DatabaseMeter,
    @Relation(
        parentColumn = "meterId",
        entityColumn = "parentMeterId"
    )
    val databaseMeterReadings: List<DatabaseMeterReading>
)