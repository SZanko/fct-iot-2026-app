package pt.nova.fct.iot.navigation.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bus_stops")
data class BusStopEntity(
    @PrimaryKey val id: Long,
    val name: String?,
    val reference: String?,
    val latitude: Double,
    val longitude: Double,
)
