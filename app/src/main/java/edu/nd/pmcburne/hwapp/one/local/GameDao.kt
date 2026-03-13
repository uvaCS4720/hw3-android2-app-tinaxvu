package edu.nd.pmcburne.hwapp.one.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import edu.nd.pmcburne.hwapp.one.local.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games WHERE date = :date AND gender = :gender ORDER BY statusState DESC, startTime ASC")
    fun getGames(date: String, gender: String): Flow<List<GameEntity>>

    @Upsert
    suspend fun upsertGames(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE date = :date AND gender = :gender")
    suspend fun deleteGames(date: String, gender: String)

}