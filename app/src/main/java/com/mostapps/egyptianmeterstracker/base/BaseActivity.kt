package com.mostapps.egyptianmeterstracker.base

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.mostapps.egyptianmeterstracker.utils.ContextUtils
import com.mostapps.egyptianmeterstracker.utils.SharedPreferencesUtils
import org.koin.android.ext.android.inject
import java.util.*

open class BaseActivity : AppCompatActivity() {

    private val sharedPreferences: SharedPreferencesUtils by inject()
    override fun attachBaseContext(newBase: Context) {

        val localeToSwitchTo = Locale(sharedPreferences.getAppLanguage())
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

}