package com.example.chelasreversi.preferences.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.chelasreversi.game.GameSummary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


open class FavoriteGameRepository(private val dataStore: DataStore<Preferences>) {
    val FAVORITE_GAMES_KEY = stringSetPreferencesKey("favorite_games")

    open suspend fun getFavoriteGames(): List<GameSummary> {
        val preferences = dataStore.data.map { it[FAVORITE_GAMES_KEY] ?: emptySet() }.first()
        return preferences.map { GameSummary(it) }
    }

    open suspend fun addFavorite(gameSummary: GameSummary) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_GAMES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(gameSummary.title)
            preferences[FAVORITE_GAMES_KEY] = currentFavorites
        }
    }

    open suspend fun removeFavorite(gameSummary: GameSummary) {
        dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITE_GAMES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(gameSummary.title)
            preferences[FAVORITE_GAMES_KEY] = currentFavorites
        }
    }
}
    /*

    private val favoriteGames = mutableListOf<GameSummary>()

open suspend fun getFavoriteGames(): List<GameSummary> {
        return favoriteGames.toList()
    }

    open suspend fun addFavorite(gameSummary: GameSummary) {
        favoriteGames.add(gameSummary)
    }

    open suspend fun removeFavorite(gameSummary: GameSummary) {
        favoriteGames.removeAll { it.title == gameSummary.title }
    }
}*/