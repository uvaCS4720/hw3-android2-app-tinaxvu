package edu.nd.pmcburne.hwapp.one.remote

data class ScoreboardResponse(
    val events: List<Event>? = null
)

data class Event(
    val id: String,
    val date: String? = null,
    val name: String? = null,
    val competitions: List<Competition>? = null
)

data class Competition(
    val id: String,
    val date: String? = null,
    val status: CompetitionStatus? = null,
    val competitors: List<Competitor>? = null,
    val situation: Situation? = null
)

data class CompetitionStatus(
    val clock: Double? = null,
    val displayClock: String? = null,
    val period: Int? = null,
    val type: StatusType? = null
)

data class StatusType(
    val id: String? = null,
    val name: String? = null,
    val state: String? = null,
    val completed: Boolean? = null,
    val description: String? = null,
    val detail: String? = null,
    val shortDetail: String? = null
)

data class Competitor(
    val id: String,
    val homeAway: String? = null,
    val winner: Boolean? = null,
    val score: String? = null,
    val team: Team? = null,
    val records: List<Record>? = null
)

data class Team(
    val id: String,
    val location: String? = null,
    val name: String? = null,
    val abbreviation: String? = null,
    val displayName: String? = null,
    val shortDisplayName: String? = null,
    val color: String? = null,
    val alternateColor: String? = null,
    val logo: String? = null
)

data class Record(
    val name: String? = null,
    val summary: String? = null,
    val type: String? = null
)

data class Situation(
    val lastPlay: LastPlay? = null
)

data class LastPlay(
    val text: String? = null
)