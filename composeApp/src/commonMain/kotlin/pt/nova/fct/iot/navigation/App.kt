package pt.nova.fct.iot.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import pt.nova.fct.iot.navigation.ui.BusStopTimeTable

@Composable
@Preview
fun App() {
    MaterialTheme {
        BusStopTimeTable()
    }
}
