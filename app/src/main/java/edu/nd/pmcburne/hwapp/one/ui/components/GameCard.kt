package edu.nd.pmcburne.hwapp.one.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.nd.pmcburne.hwapp.one.local.GameEntity

@Composable
fun GameCard(game: GameEntity, isWomens: Boolean) {
    val statusState = game.statusState
    val isLive = statusState == "in"
    val isFinal = statusState == "post" || game.isCompleted
    val isPre = statusState == "pre"

    val cardColor by animateColorAsState(
        targetValue = when {
            isLive -> MaterialTheme.colorScheme.primaryContainer
            isFinal -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.surface
        },
        label = "cardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isLive) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status badge row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(game = game, isWomens = isWomens, isLive = isLive, isFinal = isFinal)

                if (isPre) {
                    Text(
                        text = game.startTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams and scores
            TeamRow(
                teamName = game.awayTeamDisplayName,
                abbreviation = game.awayTeamAbbreviation,
                score = game.awayScore,
                isWinner = game.awayWinner,
                showScore = !isPre
            )

            Spacer(modifier = Modifier.height(8.dp))

            TeamRow(
                teamName = game.homeTeamDisplayName,
                abbreviation = game.homeTeamAbbreviation,
                score = game.homeScore,
                isWinner = game.homeWinner,
                showScore = !isPre,
                isHome = true
            )
        }
    }
}

@Composable
fun StatusBadge(game: GameEntity, isWomens: Boolean, isLive: Boolean, isFinal: Boolean) {
    val periodLabel = if (isWomens) {
        when (game.period) {
            1 -> "1st Qtr"
            2 -> "2nd Qtr"
            3 -> "3rd Qtr"
            4 -> "4th Qtr"
            else -> "OT"
        }
    } else {
        when (game.period) {
            1 -> "1st Half"
            2 -> "2nd Half"
            else -> if (game.period > 2) "OT" else ""
        }
    }

    val badgeText = when {
        isFinal -> "FINAL"
        isLive -> "${game.displayClock} - $periodLabel"
        else -> "UPCOMING"
    }

    val badgeColor = when {
        isFinal -> MaterialTheme.colorScheme.secondary
        isLive -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = badgeText,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamRow(
    teamName: String,
    abbreviation: String,
    score: String,
    isWinner: Boolean,
    showScore: Boolean,
    isHome: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (isHome) {
                Text(
                    text = "🏠 ",
                    fontSize = 12.sp
                )
            } else {
                Spacer(modifier = Modifier.width(20.dp))
            }

            Text(
                text = teamName.ifEmpty { abbreviation },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        if (showScore) {
            Text(
                text = score,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = if (isWinner) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isWinner) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}