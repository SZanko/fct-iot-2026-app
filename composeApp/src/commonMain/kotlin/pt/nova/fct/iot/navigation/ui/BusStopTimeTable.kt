package pt.nova.fct.iot.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform
import pt.nova.fct.iot.navigation.dto.carris.Arrival
import pt.nova.fct.iot.navigation.services.NearestBusStopResult
import pt.nova.fct.iot.navigation.services.NearestBusStopService

@Composable
@Preview
fun BusStopTimeTable(
    service: NearestBusStopService? = rememberNearestBusStopService(),
) {
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf<BusStopUiState>(BusStopUiState.Idle) }
    val isLoading = state is BusStopUiState.Loading

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            onClick = {
                scope.launch {
                    state = BusStopUiState.Loading
                    state = runCatching {
                        requireNotNull(service) { "App services are not initialized yet." }
                            .getNearestBusStopArrivals()
                    }.fold(
                        onSuccess = BusStopUiState::Success,
                        onFailure = { error -> BusStopUiState.Error(error.message ?: "Could not load arrivals.") },
                    )
                }
            },
        ) {
            Text(if (isLoading) "Loading nearest bus stop" else "Get nearest bus stop")
        }

        when (val currentState = state) {
            BusStopUiState.Idle -> EmptyState()
            BusStopUiState.Loading -> LoadingState()
            is BusStopUiState.Error -> ErrorState(currentState.message)
            is BusStopUiState.Success -> ArrivalsList(currentState.result)
        }
    }
}

@Composable
private fun rememberNearestBusStopService(): NearestBusStopService? {
    return remember {
        runCatching { KoinPlatform.getKoin().get<NearestBusStopService>() }.getOrNull()
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No timetable loaded",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(modifier = Modifier.size(36.dp))
            Text(
                text = "Finding arrivals",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ErrorState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ArrivalsList(result: NearestBusStopResult) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = result.stopName,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Stop ${result.stopId} - ${result.arrivals.size} arrivals",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (result.arrivals.isEmpty()) {
            Text(
                text = "No arrivals returned for this stop",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = result.arrivals,
                    key = { arrival -> arrival.tripId ?: "${arrival.lineId}-${arrival.headsign}-${arrival.scheduledArrival}" },
                ) { arrival ->
                    ArrivalRow(arrival)
                }
            }
        }
    }
}

@Composable
private fun ArrivalRow(arrival: Arrival) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = arrival.lineId,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = arrival.bestTimeLabel(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.74f),
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = arrival.headsign,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = arrival.vehicleId?.let { vehicle -> "Vehicle $vehicle" } ?: "Vehicle not assigned",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun Arrival.bestTimeLabel(): String {
    return estimatedArrival ?: observedArrival ?: scheduledArrival ?: "--:--"
}

private sealed interface BusStopUiState {
    data object Idle : BusStopUiState
    data object Loading : BusStopUiState
    data class Success(val result: NearestBusStopResult) : BusStopUiState
    data class Error(val message: String) : BusStopUiState
}
