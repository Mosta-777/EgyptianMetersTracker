package com.mostapps.egyptianmeterstracker.base

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.mostapps.egyptianmeterstracker.utils.ContextUtils
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // get chosen language from shared preference


        val localeToSwitchTo: Locale = Locale("ar")
        val localeUpdatedContext: ContextWrapper =
            ContextUtils.updateLocale(newBase, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

}