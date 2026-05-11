package dev.maxine.librelune

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dev.maxine.librelune.work.DailyRefreshWorker
import java.util.concurrent.TimeUnit

class LibreluneApp : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleDailyRefresh()
    }

    private fun scheduleDailyRefresh() {
        val request = PeriodicWorkRequestBuilder<DailyRefreshWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "librelune_daily_refresh",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
