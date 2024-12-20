package com.example.chelasreversi.game.play.domain

import PlayerInfo
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.lobby.domain.firstToMove


data class Game(
    val localPlayer: Player = Player.firstToMove,
    val forfeitedBy: Player? = null,
    val board: Board = Board(),
    val gameId: String
)

fun Game.makeMove(coordinates: Coordinates): Game {
    check(localPlayer == board.turn)
    return copy(
        localPlayer = localPlayer.other,
        board = board.makeMove(coordinates)
    )
}

fun getLocalPlayerMarker(localPlayer: PlayerInfo, challenge: Challenge) =
    if (localPlayer == challenge.firstToMove) Player.firstToMove
    else Player.firstToMove.other

fun Game.getResult() =
    if (forfeitedBy != null) Board.HasWinner(forfeitedBy.other)
    else board.getResult()

fun Game.toFavorites() =
    copy(localPlayer = Player.firstToMove, forfeitedBy = null, board = board.toFavorites())