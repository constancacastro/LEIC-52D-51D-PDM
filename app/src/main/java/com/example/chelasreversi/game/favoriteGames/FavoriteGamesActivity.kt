package com.example.chelasreversi.game.favoriteGames

import FavoriteGamesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.chelasreversi.DependenciesContainer
import com.example.chelasreversi.game.favoriteGames.replayGames.ReplayGameActivity

class FavoriteGamesActivity : ComponentActivity() {

    private val vm by viewModels<FavoriteGameViewModel> {
        FavoriteGameViewModel.factory(
            (application as DependenciesContainer).favoriteGames
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val favoriteGames by vm.favoriteGames.collectAsState()
            FavoriteGamesScreen(
                favoriteGames = favoriteGames,
                onGameSelected = { game ->
                    ReplayGameActivity.navigateTo(this, game.title)
                },
                onBackRequested = { finish() }
            )
        }
    }
}