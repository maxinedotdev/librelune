package dev.maxine.librelune.work

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.maxine.librelune.widget.MoonWidget

class DailyRefreshWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val widget = MoonWidget()
        manager.getGlanceIds(MoonWidget::class.java).forEach { glanceId ->
            widget.update(context, glanceId)
        }
        return Result.success()
    }
}
