package com.Goldy.blindchess.utils

import android.content.Context
import android.content.SharedPreferences

class ScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chess_scores", Context.MODE_PRIVATE)

    fun saveZenHighScore(score: Int) {
        val currentHigh = getZenHighScore()
        if (score > currentHigh) {
            prefs.edit().putInt("zen_high_score", score).apply()
        }
    }

    fun getZenHighScore(): Int = prefs.getInt("zen_high_score", 0)

    fun saveBlitzHighScore(score: Int) {
        val currentHigh = getBlitzHighScore()
        if (score > currentHigh) {
            prefs.edit().putInt("blitz_high_score", score).apply()
        }
    }

    fun getBlitzHighScore(): Int = prefs.getInt("blitz_high_score", 0)
}