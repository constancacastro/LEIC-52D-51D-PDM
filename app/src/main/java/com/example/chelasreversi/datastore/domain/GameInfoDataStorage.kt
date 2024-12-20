package com.example.chelasreversi.datastore.domain

import PlayerInfo
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.adapters.toMovesList
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Coordinates
import com.example.chelasreversi.game.play.domain.Game
import com.example.chelasreversi.game.play.domain.GameEvent
import com.example.chelasreversi.game.play.domain.GameStarted
import com.example.chelasreversi.game.play.domain.Match
import com.example.chelasreversi.game.play.domain.Player
import com.example.chelasreversi.game.play.domain.getLocalPlayerMarker
import com.example.chelasreversi.game.play.domain.getResult
import com.example.chelasreversi.game.play.domain.makeMove
import com.example.chelasreversi.game.play.domain.toFavorites
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

private const val GAME_ID = "id_game"
private const val BOARD = "board"
private const val PLAYER1 = "playerBlack"
private const val PLAYER2 = "playerWhite"
private const val WINNER = "winner"
private const val TURN = "turn"
private const val ForteitedBy = "forfeitedBy"
private val favorite_game = "favorite_game"

class GameInfoDataStorage(private val store: DataStore<Preferences>) : Match {
    private val idGameKey = stringPreferencesKey(GAME_ID)
    private val boardKey = stringPreferencesKey(BOARD)
    private val player1Key = stringPreferencesKey(PLAYER1)
    private val player2Key = stringPreferencesKey(PLAYER2)
    private val winnerKey = stringPreferencesKey(WINNER)
    private val turnKey = stringPreferencesKey(TURN)
    private val forfeitedByKey = stringPreferencesKey(ForteitedBy)
    //private val favoriteGameKey = stringPreferencesKey(favorite_game)

    override fun start(localPlayer: PlayerInfo, challenge: Challenge): Flow<GameEvent> {
        val firstPlayer = getLocalPlayerMarker(localPlayer, challenge)
        val initialBoard = Board(turn = firstPlayer)
        val game = Game(localPlayer = firstPlayer, board = initialBoard, gameId = "")

        runBlocking {
            saveGame(game)
        }

        return flowOf(GameStarted(game))
    }

    override suspend fun makeMove(coordinates: Coordinates) {
        val game = getCurrentGame()
        val updatedGame = game.makeMove(coordinates)
        saveGame(updatedGame)
        Log.v("GameInfoDataStorage", "Move made at $coordinates")
    }

    override suspend fun forfeit() {
        val game = getCurrentGame()
        val forfeitedGame = game.copy(forfeitedBy = game.board.turn)
        saveGame(forfeitedGame)
        Log.v("GameInfoDataStorage", "Game forfeited by ${game.board.turn}")
    }

    override suspend fun end() {
        val game = getCurrentGame()
        val result = game.getResult()
        if (result is Board.HasWinner) {
            Log.v("GameInfoDataStorage", "Game ended. Winner: ${result.winner}")
        } else {
            Log.v("GameInfoDataStorage", "Game ended in a tie or ongoing state.")
        }
    }

    override suspend fun saveAsFavorite(game: Game) {
        val favoriteGame = game.toFavorites()
        saveGame(favoriteGame)
        Log.v("GameInfoDataStorage", "Game saved as favorite")
    }

    private suspend fun getCurrentGame(): Game {
        val preferences = store.data.first()

        val storedId = preferences[idGameKey] ?: ""
        val storedBoard =
            preferences[boardKey]?.let { Board.fromMovesList(Player.firstToMove, it.toMovesList()) }
        val storedWinner = preferences[winnerKey]?.let { Player.valueOf(it) }
        val storedTurn = preferences[turnKey] ?: Player.firstToMove.name
        val storedForfeitedBy = preferences[forfeitedByKey]?.let { Player.valueOf(it) }

        return Game(
            localPlayer = Player.valueOf(storedTurn),
            forfeitedBy = storedForfeitedBy,
            board = storedBoard ?: Board(),
            gameId = storedId
        )
    }

    private suspend fun saveGame(game: Game) {
        store.edit { preferences ->
            preferences[idGameKey] = game.hashCode().toString()
            preferences[boardKey] = game.board.toMovesList().joinToString(",")
            preferences[player1Key] = game.localPlayer.name
            preferences[player2Key] = game.board.turn.name
            preferences[winnerKey] = game.forfeitedBy?.name ?: ""
            preferences[turnKey] = game.board.turn.name
            preferences[forfeitedByKey] = game.forfeitedBy?.name ?: ""
        }
    }
}