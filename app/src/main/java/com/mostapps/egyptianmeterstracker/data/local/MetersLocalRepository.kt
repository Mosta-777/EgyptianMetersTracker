package com.mostapps.egyptianmeterstracker.data.local

import com.mostapps.egyptianmeterstracker.data.MetersDataSource
import com.mostapps.egyptianmeterstracker.data.dto.MetersDTO
import kotlinx.coroutines.*

class MetersLocalRepository(
    private val remindersDao: MetersDTO,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MetersDataSource {

}
