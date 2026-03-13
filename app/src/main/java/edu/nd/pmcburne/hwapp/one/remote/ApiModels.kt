package edu.nd.pmcburne.hwapp.one.remote

data class ScoreboardResponse(
    val games: List<GameWrapper>? = null
)

data class GameWrapper(
    val game: Game? = null
)

data class Game(
    val gameID: String,
    val title: String? = null,
    val gameState: String? = null,
    val startTime: String? = null,
    val startDate: String? = null,
    val currentPeriod: String? = null,
    val contestClock: String? = null,
    val finalMessage: String? = null,
    val home: GameTeam? = null,
    val away: GameTeam? = null
)

data class GameTeam(
    val score: String? = null,
    val winner: Boolean? = null,
    val names: TeamNames? = null
)

data class TeamNames(
    val short: String? = null,
    val char6: String? = null
)