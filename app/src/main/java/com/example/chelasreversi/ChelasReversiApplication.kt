package com.example.chelasreversi

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.chelasreversi.datastore.domain.GameInfoDataStorage
import com.example.chelasreversi.datastore.storage.UserInfoDataStorage
import com.example.chelasreversi.game.favoriteGames.FavoriteGamesFirebase
import com.example.chelasreversi.game.lobby.adapters.LobbyFirebase
import com.example.chelasreversi.game.lobby.domain.Lobby
import com.example.chelasreversi.game.play.adapters.MatchFirebase
import com.example.chelasreversi.game.play.domain.Match
import com.example.chelasreversi.preferences.domain.UserInfoRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

interface DependenciesContainer {
    val userInfoRepo: UserInfoRepository
    val lobby: Lobby
    val match: Match
    val favoriteGames: FavoriteGamesFirebase
    val preferencesDataStore: DataStore<Preferences>
    val gameInfoDataStorage: GameInfoDataStorage
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class CheRApplication : Application(), DependenciesContainer {

    private val useEmulator = true

    private val firestoreDb: FirebaseFirestore
        get() = if (useEmulator) emulatedDb else realDb

    override val preferencesDataStore: DataStore<Preferences> by lazy {
        applicationContext.dataStore
    }

    override val userInfoRepo: UserInfoRepository by lazy {
        UserInfoDataStorage(preferencesDataStore)
    }

    override val lobby: Lobby by lazy {
        LobbyFirebase(firestoreDb)
    }

    override val match: Match by lazy {
        MatchFirebase(firestoreDb)
    }

    override val favoriteGames: FavoriteGamesFirebase by lazy {
        FavoriteGamesFirebase(firestoreDb, preferencesDataStore)
    }

    override val gameInfoDataStorage: GameInfoDataStorage by lazy {
        GameInfoDataStorage(preferencesDataStore)
    }

    val emulatedDb: FirebaseFirestore by lazy {
        Firebase.firestore.also {
            it.useEmulator("10.0.2.2", 8080)
            it.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build()
        }
    }

    val realDb: FirebaseFirestore by lazy {
        Firebase.firestore
    }
}
