package com.mostapps.egyptianmeterstracker.data.local

import com.mostapps.egyptianmeterstracker.data.local.entites.MeterDTO
import com.mostapps.egyptianmeterstracker.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.MeterReadingsCollectionDTO
import com.mostapps.egyptianmeterstracker.data.local.entites.relations.MeterWithMeterReadings


class MetersLocalRepository(
    private val metersDao: MetersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetersLocalDataSource {

    override suspend fun getMeters(): Result<List<MeterDTO>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(metersDao.getAllMeters())
            } catch (ex: Exception) {
                Result.Error(ex.localizedMessage)
            }
        }
    }

    override suspend fun getMeter(id: String): Result<MeterDTO> =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                try {
                    val meter = metersDao.getMeterById(id)
                    if (meter != null) {
                        return@withContext Result.Success(meter)
                    } else {
                        return@withContext Result.Error("Meter not found!")
                    }
                } catch (e: Exception) {
                    return@withContext Result.Error(e.localizedMessage)
                }
            }
        }

    override suspend fun saveMeter(
        meter: MeterDTO,
        meterReadingsCollection: MeterReadingsCollectionDTO,
        firstMeterReading: MeterReadingDTO,
        currentMeterReading: MeterReadingDTO
    ) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.saveMeter(
                    meter,
                    meterReadingsCollection,
                    firstMeterReading,
                    currentMeterReading
                )
            }
        }

    override suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings> {
        TODO("Not yet implemented")
    }


}
