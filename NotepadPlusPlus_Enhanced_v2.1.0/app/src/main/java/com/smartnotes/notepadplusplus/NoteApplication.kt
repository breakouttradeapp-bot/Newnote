package com.smartnotes.notepadplusplus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.android.gms.ads.MobileAds
import com.smartnotes.notepadplusplus.data.database.NoteDatabase
import com.smartnotes.notepadplusplus.data.repository.NoteRepository
import com.smartnotes.notepadplusplus.utils.PreferencesManager

class NoteApplication : Application() {

    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this) }
    val repository: NoteRepository by lazy { NoteRepository(database.noteDao()) }
    val preferencesManager: PreferencesManager by lazy { PreferencesManager(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        initializeAdMob()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Note Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for note reminders"
                enableVibration(true)
            }
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun initializeAdMob() {
        // Initialize Mobile Ads SDK with callback
        MobileAds.initialize(this) { initializationStatus ->
            // Log initialization status
            val statusMap = initializationStatus.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                android.util.Log.d(
                    "AdMob",
                    "Adapter: $adapterClass, Status: ${status?.initializationState}, Description: ${status?.description}"
                )
            }
        }

        // Set request configuration for testing
        val configuration = com.google.android.gms.ads.RequestConfiguration.Builder()
            .setTestDeviceIds(
                listOf(
                    "B3EEABB8EE11C2BE770B684D95219ECB"
                    // Add your test device IDs here
                    // You can find your device ID in Logcat when you first request an ad
                )
            )
            .build()

        MobileAds.setRequestConfiguration(configuration)
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "note_reminders"
    }
}
