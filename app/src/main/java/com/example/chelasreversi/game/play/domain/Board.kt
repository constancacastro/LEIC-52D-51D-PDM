package com.example.chelasreversi.game.play.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val turn: Player = Player.firstToMove,
    val cell: List<List<Player?>> =
        List(size = BOARD_SIDE) { row ->
            List(size = BOARD_SIDE) { col ->
                when {
                    row == 3 && col == 3 -> Player.WHITE
                    row == 3 && col == 4 -> Player.BLACK
                    row == 4 && col == 3 -> Player.BLACK
                    row == 4 && col == 4 -> Player.WHITE
                    else -> null
                }
            }
        }
) : Parcelable {

    companion object {
        fun fromMovesList(turn: Player, moves: List<Player?>) = Board(
            turn = turn,
            cell = List(size = BOARD_SIDE) { row ->
                List(size = BOARD_SIDE) { col ->
                    moves[row * BOARD_SIDE + col]
                }
            }
        )
    }

    operator fun get(coordinates: Coordinates): Player? = getMove(coordinates)

    fun getMove(coordinates: Coordinates): Player? = cell[coordinates.row][coordinates.col]

    fun makeMove(coordinates: Coordinates): Board {
        check(isValidMove(coordinates)) { "Invalid move" }
        return Board(
            turn = turn.other,
            cell = cell.mapIndexed { row, elem ->
                if (row == coordinates.row)
                    List(cell[row].size) { col ->
                        if (col == coordinates.col) turn
                        else cell[row][col]
                    }
                else
                    elem
            }
        )
    }

    fun toMovesList(): List<Player?> = cell.flatten()


    fun getValidMoves(): List<Coordinates> {
        val validMoves = mutableListOf<Coordinates>()
        for (row in 0 until BOARD_SIDE) {
            for (col in 0 until BOARD_SIDE) {
                val coordinates = Coordinates(row, col)
                if (isValidMove(coordinates)) {
                    validMoves.add(coordinates)
                }
            }
        }
        return validMoves
    }

    private fun isValidMove(coordinates: Coordinates): Boolean {
        if (cell[coordinates.row][coordinates.col] != null) return false

        val directions = listOf(
            Pair(-1, 0), // Up
            Pair(1, 0),  // Down
            Pair(0, -1), // Left
            Pair(0, 1),  // Right
            Pair(-1, -1), // Up-left
            Pair(-1, 1),  // Up-right
            Pair(1, -1),  // Down-left
            Pair(1, 1)    // Down-right
        )

        for ((rowDir, colDir) in directions) {
            var row = coordinates.row + rowDir
            var col = coordinates.col + colDir
            var hasOpponentBetween = false

            while (row in 0 until BOARD_SIDE && col in 0 until BOARD_SIDE) {
                val currentCell = cell[row][col]

                if (currentCell == turn.other) {
                    hasOpponentBetween = true
                } else if (currentCell == turn && hasOpponentBetween) {
                    return true
                } else {
                    break
                }
                row += rowDir
                col += colDir
            }
        }
        return false // no valid dir
    }


    // fun Board.isGameOver(): Boolean =
    //   hasWon(Player.BLACK) || hasWon(Player.WHITE) || isDraw() || getValidMoves().isEmpty()

    private fun Board.isDraw(): Boolean =
        cell.flatten().all { it != null } && !hasWon(Player.BLACK) && !hasWon(Player.WHITE)

    private fun Board.hasWon(player: Player): Boolean {
        if (getValidMoves().isNotEmpty()) return false
        return cell.flatten().count { it == player } > (BOARD_SIDE * BOARD_SIDE / 2) + 1
    }

    open class BoardResult
    class HasWinner(val winner: Player) : BoardResult()
    class Tied : BoardResult()
    class OnGoing : BoardResult()


    fun getResult(): BoardResult =
        when {
            hasWon(Player.BLACK) -> HasWinner(Player.BLACK)
            hasWon(Player.WHITE) -> HasWinner(Player.WHITE)
            toMovesList().all { it != null } -> Tied()
            else -> OnGoing()
        }

    fun toFavorites(): Board = copy(turn = Player.firstToMove)
}
