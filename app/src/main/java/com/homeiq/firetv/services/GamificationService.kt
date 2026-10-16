package com.homeiq.firetv.services

import com.homeiq.firetv.model.Achievement
import com.homeiq.firetv.model.Challenge
import com.homeiq.firetv.model.FamilyMember
import com.homeiq.firetv.model.LeaderboardEntry
import com.homeiq.firetv.model.Streak

/**
 * GamificationService: Energy savings habits, challenges, streaks, and family leaderboards.
 * Fosters daily engagement and turns energy conservation into a fun, rewarding family experience.
 */
class GamificationService {

    fun getDefaultChallenges(): List<Challenge> {
        return listOf(
            Challenge(
                id = "CHALLENGE_01",
                title = "Daily Eco Sprint",
                description = "Keep total home load under 20 kWh today",
                progress = 0.68f,
                rewardPoints = 150,
                isCompleted = false
            ),
            Challenge(
                id = "CHALLENGE_02",
                title = "Peak Tariff Defender",
                description = "Zero AC usage during 6 PM - 8 PM peak pricing",
                progress = 1.0f,
                rewardPoints = 250,
                isCompleted = true
            ),
            Challenge(
                id = "CHALLENGE_03",
                title = "Sleep Saver",
                description = "Maintain night temperature at 26°C for 7 straight hours",
                progress = 0.45f,
                rewardPoints = 100,
                isCompleted = false
            )
        )
    }

    fun getDefaultStreak(): Streak {
        return Streak(
            days = 7,
            weeklySavingsRupees = 340.0,
            nextMilestoneDays = 10
        )
    }

    fun getDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "ACH_01",
                icon = "🏆",
                title = "First Week Champion",
                description = "Maintained 7 consecutive days below energy target",
                isUnlocked = true,
                unlockedDate = "Oct 14, 2026"
            ),
            Achievement(
                id = "ACH_02",
                icon = "🌙",
                title = "Night Owl Saver",
                description = "Reduced standby and overnight load by 35%",
                isUnlocked = true,
                unlockedDate = "Oct 12, 2026"
            ),
            Achievement(
                id = "ACH_03",
                icon = "☀️",
                title = "Solar Star",
                description = "Shifted 80% of high-power appliance runs to peak solar hours",
                isUnlocked = true,
                unlockedDate = "Oct 10, 2026"
            ),
            Achievement(
                id = "ACH_04",
                icon = "🌱",
                title = "Eco Legend (Locked)",
                description = "Save ₹1,000 in a single calendar month",
                isUnlocked = false,
                unlockedDate = null
            )
        )
    }

    fun getFamilyLeaderboard(members: List<FamilyMember>): List<LeaderboardEntry> {
        return members.mapIndexed { index, member ->
            val points = when (index) {
                0 -> 1420
                1 -> 1180
                2 -> 950
                else -> 720
            }
            val savings = when (index) {
                0 -> 48.5
                1 -> 34.2
                2 -> 22.8
                else -> 15.0
            }
            LeaderboardEntry(
                rank = index + 1,
                member = member,
                scorePoints = points,
                savingsKwh = savings
            )
        }.sortedBy { it.rank }
    }
}
