package com.example.chelasreversi.game.lobby.ui

import PlayerInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.lobby.domain.ChallengeReceived
import com.example.chelasreversi.game.lobby.domain.Lobby
import com.example.chelasreversi.game.lobby.domain.RosterUpdated
import com.example.chelasreversi.preferences.domain.UserInfoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyScreenViewModel(
    val lobby: Lobby,
    private val userInfoRepo: UserInfoRepository,
) : ViewModel() {

    private val _players = MutableStateFlow<List<PlayerInfo>>(emptyList())
    val players = _players.asStateFlow()

    private val _pendingMatch = MutableStateFlow<PendingChallenge?>(null)
    val pendingMatch = _pendingMatch.asStateFlow()

    private var lobbyMonitor: Pair<Job, PlayerInfo>? = null

    fun enterLobby(): Job? =
        if (lobbyMonitor == null) {
            val localPlayer = PlayerInfo(checkNotNull(userInfoRepo.userInfo), "")
            val eventObserver = viewModelScope.launch {
                lobby.enterAndObserve(localPlayer).collect { event ->
                    when (event) {
                        is RosterUpdated -> {
                            _players.value = event.players.filter {
                                it != localPlayer
                            }
                        }

                        is ChallengeReceived -> {
                            _pendingMatch.value = IncomingChallenge(localPlayer, event.challenge)
                        }
                    }
                }
            }
            lobbyMonitor = Pair(eventObserver, localPlayer)
            eventObserver
        } else null

    fun leaveLobby(): Job? =
        if (lobbyMonitor != null) {
            viewModelScope.launch {
                lobbyMonitor?.first?.cancel()
                lobbyMonitor = null
                _pendingMatch.value = null
                lobby.leave()
            }
        } else null

    fun sendChallenge(opponent: PlayerInfo): Job? {
        val currentMonitor = lobbyMonitor
        return if (currentMonitor != null) {
            viewModelScope.launch {
                val challenge = Challenge(
                    challenger = currentMonitor.second,
                    challenged = opponent
                )
                lobby.issueChallenge(challenge.challenged)
                _pendingMatch.value = SentChallenge(
                    localPlayer = currentMonitor.second,
                    challenge = challenge
                )
            }
        } else null
    }
}

/**
 * Sum type used to represent pending match events.
 * [IncomingChallenge] means that a match is about to start because a remote player challenged
 * the local player
 * [SentChallenge] means that a match is about to start because the local player challenged
 * another player in the lobby
 */
sealed class PendingChallenge(val localPlayer: PlayerInfo, val challenge: Challenge)

class IncomingChallenge(localPlayer: PlayerInfo, challenge: Challenge) :
    PendingChallenge(localPlayer, challenge)

class SentChallenge(localPlayer: PlayerInfo, challenge: Challenge) :
    PendingChallenge(localPlayer, challenge)