package edu.nd.pmcburne.hwapp.one.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")

data class GameEntity(
    @PrimaryKey
    val id: String,
    val date: String,
    val gender: String,

    // home team
    val homeTeamId: String,
    val homeTeamName: String,
    val homeTeamDisplayName: String,
    val homeTeamAbbreviation: String,
    val homeScore: String,
    val homeWinner: Boolean,

    // away team
    val awayTeamId: String,
    val awayTeamName: String,
    val awayTeamDisplayName: String,
    val awayTeamAbbreviation: String,
    val awayScore: String,
    val awayWinner: Boolean,

    // game status
    val statusState: String,
    val statusDescription: String,
    val statusDetail: String,
    val statusShortDetail: String,
    val displayClock: String,
    val period: Int,
    val isCompleted: Boolean,

    // date/time for upcoming games
    val startTime: String
)