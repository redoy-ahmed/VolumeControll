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
import com.redoy.volumecontroll.core.domain.model.VolumeSchedule
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VolumeScheduleScreen(
    viewModel: VolumeScheduleViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Volume Schedules") }
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
            Text(
                text = "Automatically apply volume profiles at specific times.",
                style = MaterialTheme.typography.bodyMedium
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(schedules) { schedule ->
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
                                Text(
                                    text = "Profile: ${schedule.profileName}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%02d:%02d", schedule.hour, schedule.minute),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Switch(
                                checked = schedule.isEnabled,
                                onCheckedChange = { enabled ->
                                    viewModel.saveSchedule(schedule.copy(isEnabled = enabled))
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val newSchedule = VolumeSchedule(
                        id = System.currentTimeMillis().toString(),
                        profileName = "Silent",
                        hour = 23,
                        minute = 0,
                        isEnabled = true
                    )
                    viewModel.saveSchedule(newSchedule)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Test Schedule")
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
