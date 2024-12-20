package com.example.chelasreversi.game.play.domain

/**
 * The Tic-Tac-Toe's board side
 */
const val BOARD_SIDE = 8

/**
 * Represents coordinates in the Tic-Tac-Toe board
 */
data class Coordinates(val row: Int, val col: Int) {
    init {
        require(isValidRow(row) && isValidColumn(col))
    }
}

fun isValidRow(value: Int) = value in 0 until BOARD_SIDE

fun isValidColumn(value: Int) = value in 0 until BOARD_SIDE
