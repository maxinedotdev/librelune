package dev.maxine.librelune.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_settings")

private fun stringKey(id: Int, suffix: String) = stringPreferencesKey("widget_${id}_$suffix")
private fun boolKey(id: Int, suffix: String) = booleanPreferencesKey("widget_${id}_$suffix")

class WidgetSettingsRepo(private val context: Context) {

    fun flow(appWidgetId: Int): Flow<WidgetSettings> =
        context.dataStore.data.map { prefs -> prefs.toSettings(appWidgetId) }

    suspend fun read(appWidgetId: Int): WidgetSettings =
        context.dataStore.data.first().toSettings(appWidgetId)

    suspend fun write(appWidgetId: Int, settings: WidgetSettings) {
        context.dataStore.edit { prefs ->
            prefs[stringKey(appWidgetId, "style")] = settings.style.name
            prefs[boolKey(appWidgetId, "show_phase")] = settings.showPhaseName
            prefs[boolKey(appWidgetId, "show_illum")] = settings.showIllumination
            prefs[boolKey(appWidgetId, "show_days_full")] = settings.showDaysToFull
            prefs[boolKey(appWidgetId, "show_days_new")] = settings.showDaysToNew
            prefs[stringKey(appWidgetId, "hemisphere")] = settings.hemisphere.name
            prefs[stringKey(appWidgetId, "icon_padding_dp")] = settings.iconPaddingDp.toString()
            prefs[stringKey(appWidgetId, "line_stroke_dp")] = settings.lineStrokeDp.toString()
            prefs[stringKey(appWidgetId, "moon_diameter_pct")] = settings.moonDiameterPct.toString()
            prefs[boolKey(appWidgetId, "wobble_enabled")] = settings.wobbleEnabled
            prefs[stringKey(appWidgetId, "latitude_deg")] = settings.latitudeDeg.toString()
            prefs[stringKey(appWidgetId, "longitude_deg")] = settings.longitudeDeg.toString()
        }
    }

    private fun Preferences.toSettings(id: Int): WidgetSettings {
        val defaults = WidgetSettings()
        return WidgetSettings(
            style = this[stringKey(id, "style")]?.let { runCatching { WidgetStyle.valueOf(it) }.getOrNull() }
                ?: defaults.style,
            showPhaseName = this[boolKey(id, "show_phase")] ?: defaults.showPhaseName,
            showIllumination = this[boolKey(id, "show_illum")] ?: defaults.showIllumination,
            showDaysToFull = this[boolKey(id, "show_days_full")] ?: defaults.showDaysToFull,
            showDaysToNew = this[boolKey(id, "show_days_new")] ?: defaults.showDaysToNew,
            hemisphere = this[stringKey(id, "hemisphere")]?.let { runCatching { Hemisphere.valueOf(it) }.getOrNull() }
                ?: defaults.hemisphere,
            iconPaddingDp = this[stringKey(id, "icon_padding_dp")]?.toIntOrNull()?.coerceIn(0, 24)
                ?: defaults.iconPaddingDp,
            lineStrokeDp = this[stringKey(id, "line_stroke_dp")]?.toIntOrNull()?.coerceIn(1, 8)
                ?: defaults.lineStrokeDp,
            moonDiameterPct = this[stringKey(id, "moon_diameter_pct")]?.toIntOrNull()?.coerceIn(40, 100)
                ?: defaults.moonDiameterPct,
            wobbleEnabled = this[boolKey(id, "wobble_enabled")] ?: defaults.wobbleEnabled,
            latitudeDeg = this[stringKey(id, "latitude_deg")]?.toDoubleOrNull()?.coerceIn(-90.0, 90.0)
                ?: defaults.latitudeDeg,
            longitudeDeg = this[stringKey(id, "longitude_deg")]?.toDoubleOrNull()?.coerceIn(-180.0, 180.0)
                ?: defaults.longitudeDeg,
        )
    }
}
