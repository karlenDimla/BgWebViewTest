package com.bgwebviewtest.pirtest.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(simulationUrl: SimulationUrl)

    @Query("SELECT * FROM simulationurls WHERE url = :url")
    fun getUrl(url: String): SimulationUrl?

    @Query("SELECT * FROM simulationurls")
    fun getAllUrlsFlow(): Flow<List<SimulationUrl>>

    @Query("SELECT * FROM simulationurls")
    fun getAllUrls(): List<SimulationUrl>

    @Query("delete from simulationurls")
    fun deleteUrls()
}

@Entity(tableName = "simulationurls")
data class SimulationUrl(
    @PrimaryKey val url: String,
    val checked: Boolean,
    val checkedTimeInMillis: Long,
    val completed: Boolean,
    val completedTimeInMillis: Long,
)