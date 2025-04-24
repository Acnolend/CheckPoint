package com.example.checkpoint.core.store

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LanguageStore {
    private var currentLanguage: Locale = Locale.getDefault()


    fun getCurrentLanguage(): Locale {
        return currentLanguage
    }

    fun setLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        currentLanguage = locale
    }
}