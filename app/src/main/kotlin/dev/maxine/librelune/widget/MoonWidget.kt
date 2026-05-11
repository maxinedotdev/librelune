package dev.maxine.librelune.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.data.WidgetSettingsRepo
import dev.maxine.librelune.data.WidgetStyle
import dev.maxine.librelune.moon.MoonCalculator
import dev.maxine.librelune.ui.MainActivity
import dev.maxine.librelune.widget.styles.GraphicsStyle
import dev.maxine.librelune.widget.styles.LineStyle
import dev.maxine.librelune.widget.styles.Material3Style

class MoonWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)
        val repo = WidgetSettingsRepo(context)

        provideContent {
            val settings by repo.flow(appWidgetId).collectAsState(initial = WidgetSettings())
            val state = MoonCalculator().now()

            val clickAction = actionStartActivity(
                Intent(context, MainActivity::class.java).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )

            GlanceTheme {
                when (settings.style) {
                    WidgetStyle.LINE -> LineStyle(state, settings, clickAction)
                    WidgetStyle.MATERIAL3 -> Material3Style(state, settings, clickAction)
                    WidgetStyle.GRAPHICS -> GraphicsStyle(state, settings, clickAction)
                }
            }
        }
    }
}

class MoonWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MoonWidget()
}
