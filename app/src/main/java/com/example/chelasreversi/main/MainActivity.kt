package com.example.chelasreversi.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.DependenciesContainer
import com.example.chelasreversi.authors.AuthorsScreenActivity
import com.example.chelasreversi.game.favoriteGames.FavoriteGamesActivity
import com.example.chelasreversi.game.lobby.ui.LobbyActivity

class MainActivity : ComponentActivity() {

    private val repo by lazy {
        (application as DependenciesContainer).userInfoRepo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MainScreen(
                    modifier = Modifier
                        .padding(30.dp)
                        .width(15.dp),
                    onPlayClick = { startGame() },
                    onAuthorsClick = { authorsInfo() },
                    onFavoriteGamesClick = { favoriteGames() },
                )
            }
        }
    }

    private fun startGame() {
        startActivity(Intent(this, LobbyActivity::class.java))
    }

    private fun favoriteGames() {
        startActivity(Intent(this, FavoriteGamesActivity::class.java))
    }

    private fun authorsInfo() {
        startActivity(Intent(this, AuthorsScreenActivity::class.java))
    }
}