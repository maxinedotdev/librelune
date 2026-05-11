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

class WidgetSettingsRepo(private val context: Context) {

    private fun styleKey(id: Int) = stringPreferencesKey("widget_${id}_style")
    private fun showPhaseKey(id: Int) = booleanPreferencesKey("widget_${id}_show_phase")
    private fun showIllumKey(id: Int) = booleanPreferencesKey("widget_${id}_show_illum")
    private fun showDaysFullKey(id: Int) = booleanPreferencesKey("widget_${id}_show_days_full")
    private fun showDaysNewKey(id: Int) = booleanPreferencesKey("widget_${id}_show_days_new")
    private fun hemisphereKey(id: Int) = stringPreferencesKey("widget_${id}_hemisphere")
    private fun iconPaddingKey(id: Int) = stringPreferencesKey("widget_${id}_icon_padding_dp")
    private fun lineStrokeKey(id: Int) = stringPreferencesKey("widget_${id}_line_stroke_dp")
    private fun moonDiameterKey(id: Int) = stringPreferencesKey("widget_${id}_moon_diameter_pct")
    private fun wobbleEnabledKey(id: Int) = booleanPreferencesKey("widget_${id}_wobble_enabled")
    private fun latitudeKey(id: Int) = stringPreferencesKey("widget_${id}_latitude_deg")
    private fun longitudeKey(id: Int) = stringPreferencesKey("widget_${id}_longitude_deg")

    fun flow(appWidgetId: Int): Flow<WidgetSettings> =
        context.dataStore.data.map { prefs -> prefs.toSettings(appWidgetId) }

    suspend fun read(appWidgetId: Int): WidgetSettings =
        context.dataStore.data.first().toSettings(appWidgetId)

    suspend fun write(appWidgetId: Int, settings: WidgetSettings) {
        context.dataStore.edit { prefs ->
            prefs[styleKey(appWidgetId)] = settings.style.name
            prefs[showPhaseKey(appWidgetId)] = settings.showPhaseName
            prefs[showIllumKey(appWidgetId)] = settings.showIllumination
            prefs[showDaysFullKey(appWidgetId)] = settings.showDaysToFull
            prefs[showDaysNewKey(appWidgetId)] = settings.showDaysToNew
            prefs[hemisphereKey(appWidgetId)] = settings.hemisphere.name
            prefs[iconPaddingKey(appWidgetId)] = settings.iconPaddingDp.toString()
            prefs[lineStrokeKey(appWidgetId)] = settings.lineStrokeDp.toString()
            prefs[moonDiameterKey(appWidgetId)] = settings.moonDiameterPct.toString()
            prefs[wobbleEnabledKey(appWidgetId)] = settings.wobbleEnabled
            prefs[latitudeKey(appWidgetId)] = settings.latitudeDeg.toString()
            prefs[longitudeKey(appWidgetId)] = settings.longitudeDeg.toString()
        }
    }

    suspend fun delete(appWidgetId: Int) {
        context.dataStore.edit { prefs ->
            prefs.remove(styleKey(appWidgetId))
            prefs.remove(showPhaseKey(appWidgetId))
            prefs.remove(showIllumKey(appWidgetId))
            prefs.remove(showDaysFullKey(appWidgetId))
            prefs.remove(showDaysNewKey(appWidgetId))
            prefs.remove(hemisphereKey(appWidgetId))
            prefs.remove(iconPaddingKey(appWidgetId))
            prefs.remove(lineStrokeKey(appWidgetId))
            prefs.remove(moonDiameterKey(appWidgetId))
            prefs.remove(wobbleEnabledKey(appWidgetId))
            prefs.remove(latitudeKey(appWidgetId))
            prefs.remove(longitudeKey(appWidgetId))
        }
    }

    private fun Preferences.toSettings(id: Int) = WidgetSettings(
        style = this[styleKey(id)]?.let { runCatching { WidgetStyle.valueOf(it) }.getOrNull() }
            ?: WidgetSettings().style,
        showPhaseName = this[showPhaseKey(id)] ?: WidgetSettings().showPhaseName,
        showIllumination = this[showIllumKey(id)] ?: WidgetSettings().showIllumination,
        showDaysToFull = this[showDaysFullKey(id)] ?: WidgetSettings().showDaysToFull,
        showDaysToNew = this[showDaysNewKey(id)] ?: WidgetSettings().showDaysToNew,
        hemisphere = this[hemisphereKey(id)]?.let { runCatching { Hemisphere.valueOf(it) }.getOrNull() }
            ?: WidgetSettings().hemisphere,
        iconPaddingDp = this[iconPaddingKey(id)]?.toIntOrNull()?.coerceIn(0, 24)
            ?: WidgetSettings().iconPaddingDp,
        lineStrokeDp = this[lineStrokeKey(id)]?.toIntOrNull()?.coerceIn(1, 8)
            ?: WidgetSettings().lineStrokeDp,
        moonDiameterPct = this[moonDiameterKey(id)]?.toIntOrNull()?.coerceIn(40, 100)
            ?: WidgetSettings().moonDiameterPct,
        wobbleEnabled = this[wobbleEnabledKey(id)] ?: WidgetSettings().wobbleEnabled,
        latitudeDeg = this[latitudeKey(id)]?.toDoubleOrNull()?.coerceIn(-90.0, 90.0)
            ?: WidgetSettings().latitudeDeg,
        longitudeDeg = this[longitudeKey(id)]?.toDoubleOrNull()?.coerceIn(-180.0, 180.0)
            ?: WidgetSettings().longitudeDeg,
    )
}
