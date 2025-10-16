package com.example.playlistmaker

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor

class App : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        // Глобальный обработчик непойманных исключений
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("App", "Uncaught exception in thread: ${thread.name}", throwable)
            throwable.printStackTrace()
            // Можно добавить отправку ошибки в аналитику
        }

        settingsInteractor = Creator.provideSettingsInteractor(this)
        applyTheme()
    }

    private fun applyTheme() {
        val themeSettings = settingsInteractor.getThemeSettings()
        if (themeSettings.isThemeSet) {
            if (themeSettings.isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}