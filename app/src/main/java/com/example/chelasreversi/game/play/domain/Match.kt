package com.example.chelasreversi.game.play.domain

import PlayerInfo
import com.example.chelasreversi.game.lobby.domain.Challenge
import kotlinx.coroutines.flow.Flow

sealed class GameEvent(val game: Game)
class GameStarted(game: Game) : GameEvent(game)
class MoveMade(game: Game) : GameEvent(game)
class GameEnded(game: Game, val winner: Player? = null) : GameEvent(game)

interface Match {
    //val events: Flow<GameEvent>
    fun start(localPlayer: PlayerInfo, challenge: Challenge): Flow<GameEvent>
    suspend fun makeMove(coordinates: Coordinates)
    suspend fun forfeit()
    suspend fun end()
    suspend fun saveAsFavorite(game: Game)
}
