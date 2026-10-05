package com.redoy.volumecontroll.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redoy.volumecontroll.core.ui.components.AppVolumePanel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerAppVolumeScreen(
    viewModel: PerAppVolumeViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rules by viewModel.rules.collectAsState()
    val activeApps by viewModel.activeApps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Per-App Volume Control") }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppVolumePanel(
                apps = activeApps,
                onVolumeChanged = { pkg, vol ->
                    viewModel.setAppVolume(pkg, vol.toInt())
                }
            )

            Text(
                text = "Configured Per-App Rules",
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rules) { rule ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = rule.appName, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Target Volume: ${rule.targetVolume}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Switch(
                                checked = rule.isEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.saveRule(rule.copy(isEnabled = enabled))
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Back")
            }
        }
    }
}
