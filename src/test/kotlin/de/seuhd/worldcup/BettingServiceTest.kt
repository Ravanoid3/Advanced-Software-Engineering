package de.seuhd.worldcup

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


class BettingServiceTest {

    private fun match(id: Int, home: String, away: String, hs: Int?, aws: Int?) =
        Match(
            matchId = id,
            round = "Matchday 1",
            date = "2026-06-01",
            homeTeam = home,
            awayTeam = away,
            homeScore = hs,
            awayScore = aws,
            ground = "Test Stadium"
        )

    @BeforeTest
    fun resetBets() {
        BettingService.clear()
    }

    // ── evaluateBonus ──────────────────────────────────────────────────────────

    @Test
    fun `evaluateBonus awards 3 points for an exact score prediction`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1),     // exact match → +3
            match(2, "CCC", "DDD", 3, 0),     // no bet placed → bet-null branch
            match(3, "EEE", "FFF", 1, 1)      // home!=home exactMatch false → +0
        )
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN, 2, 1))
        // match 3: predictedHomeScore=0 != actual 1 → exactMatch false, wrong outcome → +0
        BettingService.placeBet(Bet(3, Prediction.HOME_WIN, 0, 0))

        val bonus = BettingService.evaluateBonus(matches)

        assertEquals(3, bonus) // 3 + 0 + 0
    }

    @Test
    fun `evaluateBonus awards 1 point for correct outcome without exact score`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1)
        )
        // home matches (2==2), away doesn't (0!=1) → exactMatch=false, outcome correct → +1
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN, 2, 0))

        val bonus = BettingService.evaluateBonus(matches)

        assertEquals(1, bonus)
    }

    @Test
    fun `evaluateBonus awards 0 points for a wrong prediction`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1)
        )
        // No predicted scores at all → hasExactScores=false (predictedHomeScore=null branch)
        BettingService.placeBet(Bet(1, Prediction.AWAY_WIN))

        val bonus = BettingService.evaluateBonus(matches)

        assertEquals(0, bonus)
    }

    @Test
    fun `evaluateBonus ignores unplayed matches`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", null, null),   // homeScore=null → skip
            match(2, "CCC", "DDD", 2, null),      // awayScore=null → skip
            match(3, "EEE", "FFF", 3, 0),         // played, wrong outcome → +0
            match(4, "GGG", "HHH", 1, 0)          // played, case B: predictedAwayScore=null
        )
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN, 2, 1))
        BettingService.placeBet(Bet(2, Prediction.HOME_WIN, 2, 1))
        // match 3: wrong prediction with exact scores → +0
        BettingService.placeBet(Bet(3, Prediction.AWAY_WIN, 0, 2))
        // match 4: predictedHomeScore!=null, predictedAwayScore=null → hasExactScores=false
        // outcome: HOME_WIN vs HOME_WIN → correct → +1
        BettingService.placeBet(Bet(4, Prediction.HOME_WIN, 1, null))

        val bonus = BettingService.evaluateBonus(matches)

        // unplayed matches ignored, match 3 wrong, match 4 correct without exact score
        assertEquals(1, bonus)
    }

    // ── removeBet ─────────────────────────────────────────────────────────────

    @Test
    fun `removeBet removes an existing bet so it no longer affects evaluation`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1),
            match(2, "CCC", "DDD", null, null),   // homeScore=null in evaluate
            match(3, "EEE", "FFF", 2, null)       // awayScore=null in evaluate
        )
        BettingService.placeBet(Bet(1, Prediction.HOME_WIN))
        BettingService.removeBet(1)

        val result = BettingService.evaluate(matches)

        assertEquals(0, result.evaluated)
        assertEquals(0, result.correct)
    }

    @Test
    fun `removeBet does nothing when no bet exists for that matchId`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1)
        )
        // Wrong prediction to cover the wrong-prediction branch in evaluate
        BettingService.placeBet(Bet(1, Prediction.AWAY_WIN))
        BettingService.removeBet(999) // non-existent matchId

        // First call computes and caches; second call returns cached result
        val result1 = BettingService.evaluate(matches)
        val result2 = BettingService.evaluate(matches)

        assertEquals(1, result1.evaluated)
        assertEquals(0, result1.correct)  // wrong prediction branch
        assertEquals(result1, result2)    // cached result branch
    }

    // ── changeBet ─────────────────────────────────────────────────────────────

    @Test
    fun `changeBet updates the prediction for an existing bet`() {
        //TODO("implement test")
        val matches = listOf(
            match(1, "AAA", "BBB", 2, 1)
        )
        // Start with wrong prediction
        BettingService.placeBet(Bet(1, Prediction.AWAY_WIN))
        val result1 = BettingService.evaluate(matches)
        assertEquals(0, result1.correct)  // wrong prediction branch

        // Change to correct prediction
        BettingService.changeBet(Bet(1, Prediction.HOME_WIN))

        val result2 = BettingService.evaluate(matches)
        assertEquals(1, result2.correct)  // correct after change
    }

    @Test
    fun `changeBet throws when no bet exists for that matchId`() {
        //TODO("implement test")
        assertFailsWith<IllegalArgumentException> {
            BettingService.changeBet(Bet(1, Prediction.HOME_WIN))
        }
    }
}
