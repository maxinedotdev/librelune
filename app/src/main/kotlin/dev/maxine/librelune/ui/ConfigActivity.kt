package dev.maxine.librelune.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.data.WidgetSettingsRepo
import dev.maxine.librelune.data.WidgetStyle
import dev.maxine.librelune.ui.theme.LibreluneTheme
import dev.maxine.librelune.widget.MoonWidget
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class ConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            setResult(RESULT_CANCELED)
            finish()
            return
        }

        val resultIntent = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_CANCELED, resultIntent)

        val repo = WidgetSettingsRepo(applicationContext)

        setContent {
            LibreluneTheme {
                val scope = rememberCoroutineScope()
                ConfigScreen(
                    appWidgetId = appWidgetId,
                    repo = repo,
                    onSave = { settings ->
                        scope.launch {
                            repo.write(appWidgetId, settings)
                            val glanceId = GlanceAppWidgetManager(applicationContext)
                                .getGlanceIdBy(appWidgetId)
                            MoonWidget().update(applicationContext, glanceId)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    },
                    onCancel = { finish() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigScreen(
    appWidgetId: Int,
    repo: WidgetSettingsRepo,
    onSave: (WidgetSettings) -> Unit,
    onCancel: () -> Unit,
) {
    val defaults = WidgetSettings()
    var style by remember { mutableStateOf(defaults.style) }
    var showPhaseName by remember { mutableStateOf(defaults.showPhaseName) }
    var showIllumination by remember { mutableStateOf(defaults.showIllumination) }
    var showDaysToFull by remember { mutableStateOf(defaults.showDaysToFull) }
    var showDaysToNew by remember { mutableStateOf(defaults.showDaysToNew) }
    var hemisphere by remember { mutableStateOf(defaults.hemisphere) }

    LaunchedEffect(appWidgetId) {
        val saved = repo.read(appWidgetId)
        style = saved.style
        showPhaseName = saved.showPhaseName
        showIllumination = saved.showIllumination
        showDaysToFull = saved.showDaysToFull
        showDaysToNew = saved.showDaysToNew
        hemisphere = saved.hemisphere
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Configure Widget") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Style", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                WidgetStyle.entries.forEachIndexed { index, s ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index, WidgetStyle.entries.size),
                        onClick = { style = s },
                        selected = style == s,
                        label = { Text(s.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    )
                }
            }

            Text("Show details", style = MaterialTheme.typography.labelLarge)
            SwitchRow("Phase name", showPhaseName) { showPhaseName = it }
            SwitchRow("Illumination %", showIllumination) { showIllumination = it }
            SwitchRow("Days to full moon", showDaysToFull) { showDaysToFull = it }
            SwitchRow("Days to new moon", showDaysToNew) { showDaysToNew = it }

            Text("Hemisphere", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                Hemisphere.entries.forEachIndexed { index, h ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index, Hemisphere.entries.size),
                        onClick = { hemisphere = h },
                        selected = hemisphere == h,
                        label = { Text(h.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    )
                }
            }
            Text(
                "Determines which side of the moon appears illuminated.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
                Button(onClick = {
                    onSave(
                        WidgetSettings(
                            style = style,
                            showPhaseName = showPhaseName,
                            showIllumination = showIllumination,
                            showDaysToFull = showDaysToFull,
                            showDaysToNew = showDaysToNew,
                            hemisphere = hemisphere,
                        )
                    )
                }) { Text("Save") }
            }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
