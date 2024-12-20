package com.example.chelasreversi.game.play.domain

enum class Player {
    WHITE,
    BLACK;

    companion object {
        val firstToMove: Player = BLACK
    }

    val other: Player
        get() = if (this == BLACK) WHITE else BLACK

}
