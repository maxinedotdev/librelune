package dev.maxine.librelune.ui

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import dev.maxine.librelune.data.Hemisphere
import dev.maxine.librelune.data.WidgetSettings
import dev.maxine.librelune.data.WidgetSettingsRepo
import dev.maxine.librelune.data.WidgetStyle
import dev.maxine.librelune.moon.MoonPhase
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.ui.theme.LibreluneTheme
import dev.maxine.librelune.widget.MoonGraphicsBitmapFactory
import dev.maxine.librelune.widget.MoonLineBitmapFactory
import dev.maxine.librelune.widget.MoonWidget
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt

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
    var iconPaddingDp by remember { mutableStateOf(defaults.iconPaddingDp.toFloat()) }
    var lineStrokeDp by remember { mutableStateOf(defaults.lineStrokeDp.toFloat()) }
    var moonDiameterPct by remember { mutableStateOf(defaults.moonDiameterPct.toFloat()) }
    var wobbleEnabled by remember { mutableStateOf(defaults.wobbleEnabled) }
    var latitudeDeg by remember { mutableStateOf(defaults.latitudeDeg.toFloat()) }
    var longitudeDeg by remember { mutableStateOf(defaults.longitudeDeg.toFloat()) }
    var previewPhaseFraction by remember { mutableStateOf(0.35f) }

    LaunchedEffect(appWidgetId) {
        val saved = repo.read(appWidgetId)
        style = saved.style
        showPhaseName = saved.showPhaseName
        showIllumination = saved.showIllumination
        showDaysToFull = saved.showDaysToFull
        showDaysToNew = saved.showDaysToNew
        hemisphere = saved.hemisphere
        iconPaddingDp = saved.iconPaddingDp.toFloat()
        lineStrokeDp = saved.lineStrokeDp.toFloat()
        moonDiameterPct = saved.moonDiameterPct.toFloat()
        wobbleEnabled = saved.wobbleEnabled
        latitudeDeg = saved.latitudeDeg.toFloat()
        longitudeDeg = saved.longitudeDeg.toFloat()
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
                "Hemisphere flips visual orientation. Moon phase and illumination are geocentric and only weakly location-dependent.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text("Wobble (location-based)", style = MaterialTheme.typography.labelLarge)
            SwitchRow("Enable lunar wobble", wobbleEnabled) { wobbleEnabled = it }
            Text(
                "Uses commons-suncalc MoonPosition parallactic angle. Set your coordinates manually.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (wobbleEnabled) {
                Text(
                    text = "Latitude: ${"%.1f".format(latitudeDeg)}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
                Slider(
                    value = latitudeDeg,
                    onValueChange = { latitudeDeg = it.coerceIn(-90f, 90f) },
                    valueRange = -90f..90f,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Longitude: ${"%.1f".format(longitudeDeg)}°",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
                Slider(
                    value = longitudeDeg,
                    onValueChange = { longitudeDeg = it.coerceIn(-180f, 180f) },
                    valueRange = -180f..180f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text("Icon padding", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "${iconPaddingDp.toInt()} dp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Slider(
                value = iconPaddingDp,
                onValueChange = { iconPaddingDp = it.coerceIn(0f, 24f) },
                valueRange = 0f..24f,
                modifier = Modifier.fillMaxWidth(),
            )

            Text("Moon diameter", style = MaterialTheme.typography.labelLarge)
            Text(
                text = "${moonDiameterPct.toInt()} %",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Slider(
                value = moonDiameterPct,
                onValueChange = { moonDiameterPct = it.coerceIn(40f, 100f) },
                valueRange = 40f..100f,
                modifier = Modifier.fillMaxWidth(),
            )

            if (style == WidgetStyle.LINE) {
                Text("Line thickness", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "${lineStrokeDp.toInt()} dp",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
                Slider(
                    value = lineStrokeDp,
                    onValueChange = { lineStrokeDp = it.coerceIn(1f, 8f) },
                    valueRange = 1f..8f,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Text("Phase preview", style = MaterialTheme.typography.labelLarge)
            val previewSettings = WidgetSettings(
                style = style,
                showPhaseName = showPhaseName,
                showIllumination = showIllumination,
                showDaysToFull = showDaysToFull,
                showDaysToNew = showDaysToNew,
                hemisphere = hemisphere,
                iconPaddingDp = iconPaddingDp.toInt().coerceIn(0, 24),
                lineStrokeDp = lineStrokeDp.toInt().coerceIn(1, 8),
                moonDiameterPct = moonDiameterPct.toInt().coerceIn(40, 100),
                wobbleEnabled = wobbleEnabled,
                latitudeDeg = latitudeDeg.toDouble().coerceIn(-90.0, 90.0),
                longitudeDeg = longitudeDeg.toDouble().coerceIn(-180.0, 180.0),
            )
            MoonPreviewCard(
                settings = previewSettings,
                phaseFraction = previewPhaseFraction,
            )
            Text(
                text = "Phase: ${previewPhaseLabel(previewPhaseFraction)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            Slider(
                value = previewPhaseFraction,
                onValueChange = { previewPhaseFraction = it.coerceIn(0f, 1f) },
                valueRange = 0f..1f,
                modifier = Modifier.fillMaxWidth(),
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
                            iconPaddingDp = iconPaddingDp.toInt().coerceIn(0, 24),
                            lineStrokeDp = lineStrokeDp.toInt().coerceIn(1, 8),
                            moonDiameterPct = moonDiameterPct.toInt().coerceIn(40, 100),
                            wobbleEnabled = wobbleEnabled,
                            latitudeDeg = latitudeDeg.toDouble().coerceIn(-90.0, 90.0),
                            longitudeDeg = longitudeDeg.toDouble().coerceIn(-180.0, 180.0),
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

@Composable
private fun MoonPreviewCard(settings: WidgetSettings, phaseFraction: Float) {
    val state = previewStateFromFraction(phaseFraction, settings)
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, shape)
            .clip(shape)
            .padding(12.dp),
    ) {
        when (settings.style) {
            WidgetStyle.LINE -> LinePreview(settings, state)
            WidgetStyle.GRAPHICS -> GraphicsPreview(settings, state)
        }
    }
}

@Composable
private fun LinePreview(settings: WidgetSettings, state: MoonState) {
    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val wobble = if (settings.wobbleEnabled) state.wobbleDeg else 0f
    val bitmap = remember(
        phaseFraction,
        settings.hemisphere,
        settings.lineStrokeDp,
        settings.moonDiameterPct,
        wobble,
    ) {
        MoonLineBitmapFactory.render(
            phaseFraction = phaseFraction,
            hemisphere = settings.hemisphere,
            sizePx = 420,
            strokePx = settings.lineStrokeDp.coerceIn(1, 8) * 2.1f,
            wobbleDeg = wobble,
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Line moon preview",
            modifier = Modifier
                .size(92.dp),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            if (settings.showPhaseName) Text(state.phase.shortName, style = MaterialTheme.typography.titleSmall)
            if (settings.showIllumination) Text("${state.illuminationPct}%", style = MaterialTheme.typography.bodyMedium)
            if (settings.showDaysToFull) Text("F+${state.daysToFull.toInt()}d", style = MaterialTheme.typography.bodySmall)
            if (settings.showDaysToNew) Text("N+${state.daysToNew.toInt()}d", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GraphicsPreview(settings: WidgetSettings, state: MoonState) {
    val phaseFraction = ((state.ageDays % SYNODIC_MONTH_DAYS) + SYNODIC_MONTH_DAYS) % SYNODIC_MONTH_DAYS / SYNODIC_MONTH_DAYS
    val wobble = if (settings.wobbleEnabled) state.wobbleDeg else 0f
    val bitmap = remember(phaseFraction, settings.hemisphere, wobble) {
        MoonGraphicsBitmapFactory.render(
            phaseFraction = phaseFraction,
            hemisphere = settings.hemisphere,
            sizePx = 420,
            wobbleDeg = wobble,
        )
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Graphics moon preview",
            modifier = Modifier
                .size(92.dp),
            contentScale = ContentScale.Fit,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            if (settings.showPhaseName) Text(state.phase.shortName, style = MaterialTheme.typography.titleSmall)
            if (settings.showIllumination) Text("${state.illuminationPct}%", style = MaterialTheme.typography.bodyMedium)
            if (settings.showDaysToFull) Text("F+${state.daysToFull.toInt()}d", style = MaterialTheme.typography.bodySmall)
            if (settings.showDaysToNew) Text("N+${state.daysToNew.toInt()}d", style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun previewStateFromFraction(phaseFraction: Float, settings: WidgetSettings): MoonState {
    val normalized = phaseFraction.coerceIn(0f, 1f).toDouble()
    val ageDays = normalized * SYNODIC_MONTH_DAYS
    val illuminationPct = ((1.0 - cos(2.0 * PI * normalized)) * 50.0).roundToInt().coerceIn(0, 100)
    val base = MoonState(
        phase = MoonPhase.NEW,
        illuminationPct = illuminationPct,
        ageDays = ageDays,
        daysToFull = daysUntilTarget(normalized, 0.5),
        daysToNew = daysUntilTarget(normalized, 0.0),
        wobbleDeg = if (settings.wobbleEnabled) approximateWobbleDeg(settings.latitudeDeg, normalized) else 0f,
    )

    return base.copy(phase = MoonPhase.fromAgeDays(base.ageDays))
}

private fun previewPhaseLabel(phaseFraction: Float): String {
    val state = previewStateFromFraction(phaseFraction, WidgetSettings())
    return state.phase.shortName
}

private fun daysUntilTarget(currentFraction: Double, targetFraction: Double): Double {
    val wrapped = if (targetFraction >= currentFraction) {
        targetFraction - currentFraction
    } else {
        1.0 - (currentFraction - targetFraction)
    }
    return wrapped * SYNODIC_MONTH_DAYS
}

private fun approximateWobbleDeg(latitudeDeg: Double, phaseFraction: Double): Float {
    val latFactor = (latitudeDeg.coerceIn(-90.0, 90.0) / 90.0)
    return (latFactor * 18.0 * cos(2.0 * PI * phaseFraction)).toFloat()
}

private const val SYNODIC_MONTH_DAYS = 29.530588853
