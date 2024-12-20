package com.example.chelasreversi.game.play.ui

import PlayerInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Coordinates
import com.example.chelasreversi.game.play.domain.Game
import com.example.chelasreversi.game.play.domain.GameEnded
import com.example.chelasreversi.game.play.domain.GameStarted
import com.example.chelasreversi.game.play.domain.Match
import com.example.chelasreversi.game.play.domain.Player
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class MatchState { IDLE, STARTING, STARTED, FINISHED }

class GameScreenViewModel(private val match: Match) : ViewModel() {

    private val _onGoingGame = MutableStateFlow(
        Game(
            localPlayer = Player.firstToMove,
            forfeitedBy = null,
            board = Board(),
            gameId = ""
        )
    )
    val onGoingGame = _onGoingGame.asStateFlow()

    private var _state by mutableStateOf(MatchState.IDLE)
    val state: MatchState
        get() = _state

    fun startMatch(localPlayer: PlayerInfo, challenge: Challenge): Job? =
        if (state == MatchState.IDLE) {
            _state = MatchState.STARTING
            Log.d(
                "GameScreenViewModel",
                "Starting match for $localPlayer against ${challenge.challenged.info.nickname}"
            )
            viewModelScope.launch {
                match.start(localPlayer, challenge).collect {
                    _onGoingGame.value = it.game
                    _state = when (it) {
                        is GameStarted -> {
                            Log.d("GameScreenViewModel", "Match started.")
                            MatchState.STARTED
                        }

                        is GameEnded -> {
                            Log.d("GameScreenViewModel", "Match ended.")
                            MatchState.FINISHED
                        }

                        else -> MatchState.IDLE
                    }

                    if (_state == MatchState.FINISHED) {
                        match.end()
                    }
                }
            }
        } else null

    fun makeMove(coordinates: Coordinates): Job? =
        if (state == MatchState.STARTED) {
            viewModelScope.launch {
                match.makeMove(coordinates)
            }
        } else null

    fun forfeit(): Job? =
        if (state == MatchState.STARTED) viewModelScope.launch { match.forfeit() }
        else null
}