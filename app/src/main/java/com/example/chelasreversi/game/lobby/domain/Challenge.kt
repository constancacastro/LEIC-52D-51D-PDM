package com.example.chelasreversi.game.lobby.domain

import PlayerInfo

data class Challenge(val challenger: PlayerInfo, val challenged: PlayerInfo)

val Challenge.firstToMove: PlayerInfo
    get() = challenger