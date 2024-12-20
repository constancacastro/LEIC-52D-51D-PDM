package com.example.chelasreversi.game.favoriteGames

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.chelasreversi.game.GameSummary
import com.example.chelasreversi.preferences.ui.FavoriteGameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteGameViewModel(private val repository: FavoriteGameRepository) : ViewModel() {

    private val _favoriteGames = MutableStateFlow<List<GameSummary>>(emptyList())
    val favoriteGames: StateFlow<List<GameSummary>> = _favoriteGames

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favoriteGames.value = repository.getFavoriteGames()
            } catch (e: Exception) {
                _favoriteGames.value = emptyList()  // Optionally, handle the error.
            }
        }
    }

    fun addFavorite(gameSummary: GameSummary) {
        viewModelScope.launch {
            try {
                repository.addFavorite(gameSummary)
                loadFavorites()  // Refresh the list after adding.
            } catch (e: Exception) {
                // Handle any error that occurs during the add operation.
            }
        }
    }

    fun removeFavorite(gameSummary: GameSummary) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(gameSummary)
                loadFavorites()  // Refresh the list after removal.
            } catch (e: Exception) {
                // Handle any error that occurs during the remove operation.
            }
        }
    }
    companion object {
        // Factory method to provide the ViewModel with dependencies
        fun factory(repository: FavoriteGameRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoriteGameViewModel(repository) as T
                }
            }
        }
    }
}

/*
    The ViewModelProvider.Factory has been adapted for initialization:

    @Suppress("UNCHECKED_CAST")
    fun <T> viewModelInit(block: () -> T) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return block() as T
            }
        }
*/


/*
    private val _favoriteGames = MutableStateFlow<List<GameSummary>>(emptyList())
    val favoriteGames: StateFlow<List<GameSummary>> = _favoriteGames

    init {
        loadFavorites()
    }


    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favoriteGames.value = repository.getFavoriteGames()
            } catch (e: Exception) {
                _favoriteGames.value = emptyList()
            }
        }
    }

    // Add a game to favorites and refresh the list
    fun addFavorite(gameSummary: GameSummary) {
        viewModelScope.launch {
            try {
                repository.addFavorite(gameSummary)
                loadFavorites()
            } catch (e: Exception) {
            }
        }
    }

    // Remove a game from favorites and refresh the list
    fun removeFavorite(gameSummary: GameSummary) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(gameSummary)
                loadFavorites()
            } catch (e: Exception) {
            }
        }
    }
}
*/