package com.example.chelasreversi.game.favoriteGames


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.chelasreversi.game.GameSummary
import com.example.chelasreversi.preferences.ui.FavoriteGameRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoriteGamesFirebase(
    db: FirebaseFirestore,
    dataStore: DataStore<Preferences>
) : FavoriteGameRepository(dataStore) {

    private val favoriteGamesCollection = db.collection("favoriteGames")

    override suspend fun getFavoriteGames(): List<GameSummary> {
        return try {
            val snapshot = favoriteGamesCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(GameSummary::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addFavorite(gameSummary: GameSummary) {
        try {
            favoriteGamesCollection.document(gameSummary.title).set(gameSummary).await()
        } catch (e: Exception) {
            error("Failed to add favorite game")
        }
    }

    override suspend fun removeFavorite(gameSummary: GameSummary) {
        try {
            favoriteGamesCollection.document(gameSummary.title).delete().await()
        } catch (e: Exception) {
            error("Failed to remove favorite game")
        }
    }
}
/*
class FavoriteGamesFirebase(private val db: FirebaseFirestore) : FavoriteGameRepository() {

    private val favoriteGamesCollection = db.collection("favoriteGames")

    override suspend fun getFavoriteGames(): List<GameSummary> {
        return try {
            val snapshot = favoriteGamesCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(GameSummary::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addFavorite(gameSummary: GameSummary) {
        try {
            favoriteGamesCollection.document(gameSummary.title).set(gameSummary).await()
        } catch (e: Exception) {
            error("Failed to add favorite game")
        }
    }

    override suspend fun removeFavorite(gameSummary: GameSummary) {
        try {
            favoriteGamesCollection.document(gameSummary.title).delete().await()
        } catch (e: Exception) {
            error("Failed to remove favorite game")
        }
    }
}*/