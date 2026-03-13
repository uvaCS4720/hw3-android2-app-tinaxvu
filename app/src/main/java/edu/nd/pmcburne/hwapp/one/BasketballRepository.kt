package edu.nd.pmcburne.hwapp.one

import android.content.Context
import android.net.NetworkCapabilities
import edu.nd.pmcburne.hwapp.one.local.GameDao
import edu.nd.pmcburne.hwapp.one.local.GameEntity
import edu.nd.pmcburne.hwapp.one.remote.BasketballApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

class BasketballRepository(
    private val apiService: BasketballApiService,
    private val gameDao: GameDao,
    private val context: Context
) {
    fun getGames(date: LocalDate, gender: String): Flow<List<GameEntity>> =
        gameDao.getGames(date.format(DateTimeFormatter.ISO_LOCAL_DATE), gender)

    suspend fun refreshGames(date: LocalDate, gender: String): Result<Unit> {
        if (!isNetworkAvailable()) return Result.failure(Exception("No network connection"))
        return try {
            val response = apiService.getScoreboard(
                gender,
                date.format(DateTimeFormatter.ofPattern("yyyy")),
                date.format(DateTimeFormatter.ofPattern("MM")),
                date.format(DateTimeFormatter.ofPattern("dd"))
            )
            val dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE)

            val entities = response.games?.mapNotNull { wrapper ->
                val game = wrapper.game ?: return@mapNotNull null
                val home = game.home ?: return@mapNotNull null
                val away = game.away ?: return@mapNotNull null

                // normalize gameState: "final" -> "post", "live" -> "in", "pre" -> "pre"
                val statusState = when (game.gameState?.lowercase()) {
                    "final" -> "post"
                    "live" -> "in"
                    else -> "pre"
                }

                val isCompleted = game.gameState?.lowercase() == "final"

                // period: currentPeriod is "1st", "2nd", "HALFTIME", etc.
                val period = when (game.currentPeriod?.lowercase()) {
                    "1st" -> 1
                    "2nd" -> 2
                    "3rd" -> 3
                    "4th" -> 4
                    "halftime" -> 2
                    "ot" -> 5
                    else -> 0
                }

                GameEntity(
                    id = game.gameID,
                    date = dateStr,
                    gender = gender,
                    homeTeamId = game.gameID + "_home",
                    homeTeamName = home.names?.short ?: home.names?.char6 ?: "",
                    homeTeamDisplayName = home.names?.short ?: home.names?.char6 ?: "",
                    homeTeamAbbreviation = home.names?.char6 ?: "",
                    homeScore = home.score ?: "",
                    homeWinner = home.winner ?: false,
                    awayTeamId = game.gameID + "_away",
                    awayTeamName = away.names?.short ?: away.names?.char6 ?: "",
                    awayTeamDisplayName = away.names?.short ?: away.names?.char6 ?: "",
                    awayTeamAbbreviation = away.names?.char6 ?: "",
                    awayScore = away.score ?: "",
                    awayWinner = away.winner ?: false,
                    statusState = statusState,
                    statusDescription = game.finalMessage ?: "",
                    statusDetail = game.currentPeriod ?: "",
                    statusShortDetail = game.currentPeriod ?: "",
                    displayClock = game.contestClock ?: "0:00",
                    period = period,
                    isCompleted = isCompleted,
                    startTime = game.startTime ?: ""
                )
            } ?: emptyList()

            gameDao.upsertGames(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork ?: return false) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}