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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.maxine.librelune.moon.MoonCalculator
import dev.maxine.librelune.moon.MoonState
import dev.maxine.librelune.ui.theme.LibreluneTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setContent {
            LibreluneTheme {
                val state = remember { MoonCalculator().now() }
                InfoScreen(
                    state = state,
                    onReconfigure = if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                        {
                            startActivity(
                                Intent(this, ConfigActivity::class.java).apply {
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                }
                            )
                        }
                    } else null,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoScreen(state: MoonState, onReconfigure: (() -> Unit)?) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Librelune") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Current Phase", style = MaterialTheme.typography.labelLarge)
                    HorizontalDivider()
                    InfoRow("Phase", state.phase.displayName)
                    InfoRow("Illumination", "${state.illuminationPct}%")
                    InfoRow("Age", "${state.ageDays.roundToInt()} days")
                    InfoRow("Days to full moon", "${state.daysToFull.roundToInt()} days")
                    InfoRow("Days to new moon", "${state.daysToNew.roundToInt()} days")
                }
            }

            if (onReconfigure != null) {
                Spacer(Modifier.height(4.dp))
                Button(onClick = onReconfigure, modifier = Modifier.fillMaxWidth()) {
                    Text("Reconfigure Widget")
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "Librelune — moon phase widget\nNo permissions required. All calculations are local.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}
