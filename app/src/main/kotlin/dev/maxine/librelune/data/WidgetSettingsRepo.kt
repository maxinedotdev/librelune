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
    )
}
