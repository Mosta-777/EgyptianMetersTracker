package com.mostapps.egyptianmeterstracker.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtils(val context: Context) {


    private val languageKey: String = "lang_key"
    private val arabicLanguageCode: String = "ar"
    private val englishLanguageCode: String = "en"
    private val defaultLanguage: String = arabicLanguageCode

    private val pref: SharedPreferences =
        context.getSharedPreferences("MetersAppPreferences", Context.MODE_PRIVATE)

    private fun putString(key: String, value: String) {
        pref.edit().putString(key, value).apply()
    }

    private fun getString(key: String, default: String = ""): String? {
        return pref.getString(key, default)
    }

    fun getAppLanguage(): String {
        val storedLanguage = getString(languageKey)
        return if (storedLanguage.isNullOrEmpty()) defaultLanguage else storedLanguage
    }

    fun storeAppLanguage(isEnglish: Boolean) {
        putString(languageKey, if (isEnglish) englishLanguageCode else arabicLanguageCode)
    }

}