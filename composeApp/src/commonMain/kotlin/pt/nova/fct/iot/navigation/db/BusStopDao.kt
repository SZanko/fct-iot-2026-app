package pt.nova.fct.iot.navigation.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BusStopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stops: List<BusStopEntity>)

    @Query("SELECT * FROM bus_stops ORDER BY name ASC")
    fun observeAll(): Flow<List<BusStopEntity>>

    @Query("SELECT COUNT(*) FROM bus_stops")
    suspend fun count(): Int

    @Query("DELETE FROM bus_stops")
    suspend fun clear()
}
