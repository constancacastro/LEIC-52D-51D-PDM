package com.example.chelasreversi.game.play.adapters

import PlayerInfo
import android.os.Build
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Coordinates
import com.example.chelasreversi.game.play.domain.Game
import com.example.chelasreversi.game.play.domain.GameEnded
import com.example.chelasreversi.game.play.domain.GameEvent
import com.example.chelasreversi.game.play.domain.GameStarted
import com.example.chelasreversi.game.play.domain.Match
import com.example.chelasreversi.game.play.domain.MoveMade
import com.example.chelasreversi.game.play.domain.Player
import com.example.chelasreversi.game.play.domain.getLocalPlayerMarker
import com.example.chelasreversi.game.play.domain.makeMove
import com.example.chelasreversi.utils.RealClock

class MatchFirebase(private val db: FirebaseFirestore) : Match {

    private var onGoingGame: Pair<Game, String>? = null

    private fun subscribeGameStateUpdated(
        localPlayerMarker: Player,
        gameId: String,
        flow: ProducerScope<GameEvent>
    ) =
        db.collection(ONGOING)
            .document(gameId)
            .addSnapshotListener { snapshot, error ->
                when {
                    error != null -> flow.close(error)
                    snapshot != null -> {
                        snapshot.toMatchStateOrNull()?.let {
                            val game = Game(
                                localPlayer = localPlayerMarker,
                                forfeitedBy = it.second,
                                board = it.first,
                                gameId = gameId
                            )
                            val gameEvent = when {
                                onGoingGame == null -> GameStarted(game)
                                game.forfeitedBy != null -> GameEnded(game, game.forfeitedBy.other)
                                else -> MoveMade(game)
                            }
                            onGoingGame = Pair(game, gameId)
                            flow.trySend(gameEvent)
                        }
                    }
                }
            }


    private suspend fun publishGame(game: Game, gameId: String) {
        db.collection(ONGOING)
            .document(gameId)
            .set(game.board.toDocumentContent())
            .await()
    }

    private suspend fun updateGame(game: Game, gameId: String) {
        db.collection(ONGOING)
            .document(gameId)
            .update(game.board.toDocumentContent())
            .await()
    }

    override fun start(localPlayer: PlayerInfo, challenge: Challenge): Flow<GameEvent> {
        check(onGoingGame == null)

        return callbackFlow {
            val newGame = Game(
                localPlayer = getLocalPlayerMarker(localPlayer, challenge),
                board = Board(),
                gameId = ""
            )
            val gameId = challenge.challenger.id

            if (gameId.isBlank()) {
                throw IllegalArgumentException("Invalid gameId: $gameId")
            }
            Log.d("MatchFirebase", "Starting game with Game ID: $gameId")

            var gameSubscription: ListenerRegistration? = null
            try {
                if (localPlayer == challenge.challenged)
                    publishGame(newGame, gameId)

                gameSubscription = subscribeGameStateUpdated(
                    localPlayerMarker = newGame.localPlayer,
                    gameId = gameId,
                    flow = this
                )
            } catch (e: Throwable) {
                close(e)
            }

            awaitClose {
                gameSubscription?.remove()
            }
        }
    }

    override suspend fun makeMove(coordinates: Coordinates) {
        onGoingGame = checkNotNull(onGoingGame).also {
            val game = it.copy(first = it.first.makeMove(coordinates))
            updateGame(game.first, game.second)
        }
    }

    override suspend fun forfeit() {
        onGoingGame = checkNotNull(onGoingGame).also {
            db.collection(ONGOING)
                .document(it.second)
                .update(FORFEIT_FIELD, it.first.localPlayer.name)
                .await()
        }
    }

    override suspend fun end() {
        onGoingGame = checkNotNull(onGoingGame).let {
            db.collection(ONGOING)
                .document(it.second)
                .delete()
                .await()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveAsFavorite(game: Game) {
        val favoriteData = mapOf(
            "localPlayer" to game.localPlayer.name,
            "opponent" to game.localPlayer.other.name,
            "board" to game.board.toMovesList().joinToString(separator = "") {
                when (it) {
                    Player.WHITE -> "W"
                    Player.BLACK -> "B"
                    null -> "-"
                }
            },
            "turn" to game.board.turn.name,
            "savedAt" to RealClock.now().toString()
        )
        db.collection("favoriteGames")
            .add(favoriteData)
            .await()
    }
}

const val ONGOING = "ongoing"
const val TURN_FIELD = "turn"
const val BOARD_FIELD = "board"
const val FORFEIT_FIELD = "forfeit"

fun Board.toDocumentContent() = mapOf(
    TURN_FIELD to turn.name,
    BOARD_FIELD to toMovesList().joinToString(separator = "") {
        when (it) {
            Player.WHITE -> "W"
            Player.BLACK -> "B"
            null -> "-"
        }
    }
)

fun DocumentSnapshot.toMatchStateOrNull(): Pair<Board, Player?>? =
    data?.let {
        val moves = it[BOARD_FIELD] as String
        val turn = Player.valueOf(it[TURN_FIELD] as String)
        val forfeit = it[FORFEIT_FIELD] as String?
        Pair(
            first = Board.fromMovesList(turn, moves.toMovesList()),
            second = if (forfeit != null) Player.valueOf(forfeit) else null
        )
    }

fun String.toMovesList(): List<Player?> = map {
    when (it) {
        'W' -> Player.WHITE
        'B' -> Player.BLACK
        else -> null
    }
}
