package com.example.chelasreversi.game.favoriteGames.replayGames

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.chelasreversi.DependenciesContainer

class ReplayGameActivity : ComponentActivity() {

    private val vm by viewModels<ReplayGameViewModel> {
        ReplayGameViewModel.factory(
            (application as DependenciesContainer).favoriteGames,
            (application as DependenciesContainer).userInfoRepo
        )
    }

    companion object {
        private const val EXTRA_GAME_ID = "extra_game_id"

        fun navigateTo(origin: ComponentActivity, gameId: String) {
            val intent = Intent(origin, ReplayGameActivity::class.java).apply {
                putExtra(EXTRA_GAME_ID, gameId)
            }
            origin.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId: String? = intent.getStringExtra(EXTRA_GAME_ID)
        if (gameId != null) {
            vm.loadGameById(gameId)
        }

        setContent {
            ReplayGameScreen(
                viewModel = vm,
                onExit = { finish() }
            )
        }
    }

}