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
            val entities = response.events?.mapNotNull { event ->
                val competition = event.competitions?.firstOrNull() ?: return@mapNotNull null
                val home = competition.competitors?.find { it.homeAway == "home" } ?: return@mapNotNull null
                val away = competition.competitors?.find { it.homeAway == "away" } ?: return@mapNotNull null
                val status = competition.status
                val statusType = status?.type
                val startTime = try {
                    ZonedDateTime.parse(competition.date ?: event.date ?: "")
                        .format(DateTimeFormatter.ofPattern("h:mm a z"))
                } catch (e: DateTimeParseException) { competition.date ?: event.date ?: "" }

                GameEntity(
                    id = event.id, date = dateStr, gender = gender,
                    homeTeamId = home.team?.id ?: "",
                    homeTeamName = home.team?.name ?: "",
                    homeTeamDisplayName = home.team?.displayName ?: home.team?.shortDisplayName ?: "",
                    homeTeamAbbreviation = home.team?.abbreviation ?: "",
                    homeScore = home.score ?: "0", homeWinner = home.winner ?: false,
                    awayTeamId = away.team?.id ?: "",
                    awayTeamName = away.team?.name ?: "",
                    awayTeamDisplayName = away.team?.displayName ?: away.team?.shortDisplayName ?: "",
                    awayTeamAbbreviation = away.team?.abbreviation ?: "",
                    awayScore = away.score ?: "0", awayWinner = away.winner ?: false,
                    statusState = statusType?.state ?: "pre",
                    statusDescription = statusType?.description ?: "",
                    statusDetail = statusType?.detail ?: "",
                    statusShortDetail = statusType?.shortDetail ?: "",
                    displayClock = status?.displayClock ?: "0:00",
                    period = status?.period ?: 0,
                    isCompleted = statusType?.completed ?: false,
                    startTime = startTime
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