package com.mostapps.egyptianmeterstracker.data.local

import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterDTO
import com.mostapps.egyptianmeterstracker.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.*
import com.mostapps.egyptianmeterstracker.utils.Result
import com.mostapps.egyptianmeterstracker.data.dto.entities.MeterReadingDTO
import com.mostapps.egyptianmeterstracker.data.dto.entities.relations.MeterWithMeterReadings


class MetersLocalRepository(
    private val metersDao: MetersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetersDataSource {

    override suspend fun getMeters(): Result<List<MeterDTO>> = withContext(ioDispatcher) {
        wrapEspressoIdlingResource {
            return@withContext try {
                Result.Success(metersDao.getMeters())
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
        firstMeterReading: MeterReadingDTO,
        currentMeterReading: MeterReadingDTO
    ) =
        wrapEspressoIdlingResource {
            withContext(ioDispatcher) {
                metersDao.saveMeter(meter, firstMeterReading, currentMeterReading)
            }
        }

    override suspend fun getMeterWithMeterReadings(id: String): Result<MeterWithMeterReadings> {
        TODO("Not yet implemented")
    }


}
