package de.seuhd.worldcup

import kotlinx.serialization.Serializable

/** Root document parsed from `world_cup_2026_full_data.json`. */
@Serializable
data class WorldCupData(
    val tournament: String,
    val groups: List<Group>,
    /** Parsed for completeness; the group phase is the scope of the assignment. */
    val knockouts: List<Knockout>
)

/** A single group, including the four teams in it and their six round-robin matches. */
@Serializable
data class Group(
    val name: String,
    val teams: List<Team>,
    val matches: List<Match>
)

/** A national team. [id] is the short code used to cross-reference matches. */
@Serializable
data class Team(
    val id: String,
    val name: String
)

/**
 * A group-phase match. [homeScore] and [awayScore] are `null` for matches that have
 * not been played yet; either both are present or neither is.
 */
@Serializable
data class Match(
    val matchId: Int,
    val round: String,
    val date: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val ground: String
)

/** `(home, away)` if the match has been played, otherwise `null`. */
fun Match.scoreOrNull(): Pair<Int, Int>? {
    val home = homeScore ?: return null
    val away = awayScore ?: return null
    return home to away
}

/**
 * A knockout-stage match. [homePlaceholder] / [awayPlaceholder] reference earlier
 * results (e.g. `"1A"` = winner of Group A) until the bracket is filled in.
 */
@Serializable
data class Knockout(
    val matchId: Int,
    val round: String,
    val date: String,
    val homePlaceholder: String,
    val awayPlaceholder: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val ground: String
)
