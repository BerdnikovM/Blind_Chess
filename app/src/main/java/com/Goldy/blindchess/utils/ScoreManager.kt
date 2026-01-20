package com.Goldy.blindchess.utils

import android.content.Context
import android.content.SharedPreferences

class ScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chess_scores", Context.MODE_PRIVATE)

    // --- Speed Colors ---
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

    // --- The Walker (Новое) ---
    fun saveWalkerHighScore(difficulty: WalkerDifficulty, wave: Int) {
        // Ключ зависит от сложности: walker_easy_best, walker_medium_best...
        val key = "walker_${difficulty.name.lowercase()}_best"
        val currentHigh = prefs.getInt(key, 0)

        // Сохраняем, только если новая волна выше текущего рекорда
        if (wave > currentHigh) {
            prefs.edit().putInt(key, wave).apply()
        }
    }

    fun getWalkerHighScore(difficulty: WalkerDifficulty): Int {
        val key = "walker_${difficulty.name.lowercase()}_best"
        return prefs.getInt(key, 0)
    }
}