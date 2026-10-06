package dev.maxine.librelune.work

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.maxine.librelune.widget.MoonWidget

class DailyRefreshWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        MoonWidget().updateAll(context)
        return Result.success()
    }
}
