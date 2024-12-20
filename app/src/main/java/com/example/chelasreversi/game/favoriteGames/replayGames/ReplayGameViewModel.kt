package com.example.chelasreversi.game.favoriteGames.replayGames

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.preferences.domain.UserInfoRepository
import com.example.chelasreversi.preferences.ui.FavoriteGameRepository

class ReplayGameViewModel(
    private val favoriteGamesRepository: FavoriteGameRepository,
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    private val _moves = mutableListOf<Board>()
    private var currentIndex by mutableIntStateOf(0)

    val currentBoard: Board
        get() = _moves.getOrElse(currentIndex) { Board() }

    val canGoToNext: Boolean
        get() = currentIndex < _moves.size - 1

    val canGoToPrevious: Boolean
        get() = currentIndex > 0

    // Call this to load game data from the repository
    fun loadGameById(gameId: String) {
        val gameMoves = favoriteGamesRepository.FAVORITE_GAMES_KEY
    }

    fun goToNext() {
        if (canGoToNext) {
            currentIndex++
        }
    }

    fun goToPrevious() {
        if (canGoToPrevious) {
            currentIndex--
        }
    }

    fun resetReplay() {
        currentIndex = 0
    }

    // Factory for ViewModel
    companion object {
        fun factory(
            favoriteGamesRepository: FavoriteGameRepository,
            userInfoRepository: UserInfoRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReplayGameViewModel( favoriteGamesRepository, userInfoRepository) as T
            }
        }
    }
}
