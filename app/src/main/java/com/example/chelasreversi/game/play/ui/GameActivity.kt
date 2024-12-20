package com.example.chelasreversi.game.play.ui

import PlayerInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.chelasreversi.DependenciesContainer
import com.example.chelasreversi.R
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.domain.getResult
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.utils.viewModelInit
import kotlinx.android.parcel.Parcelize

class GameActivity : ComponentActivity() {

    private val viewModel by viewModels<GameScreenViewModel> {
        viewModelInit {
            val app = (application as DependenciesContainer)
            GameScreenViewModel(app.match)
        }
    }

    companion object {
        const val MATCH_INFO_EXTRA = "MATCH_INFO_EXTRA"

        fun navigate(origin: Context, localPlayer: PlayerInfo, challenge: Challenge) {
            val matchInfo = MatchInfo(localPlayer, challenge)
            origin.startActivity(
                Intent(origin, GameActivity::class.java).apply {
                    putExtra(MATCH_INFO_EXTRA, matchInfo)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchInfo

        setContent {
            val currentGame by viewModel.onGoingGame.collectAsState()
            val currentState = viewModel.state
            val titleResId = when (currentState) {
                MatchState.STARTING, MatchState.IDLE -> R.string.game_screen_waiting
                else -> null
            }

            GameScreen(
                state = GameScreenState(titleResId, currentGame),
                onMoveRequested = viewModel::makeMove,
                onForfeitRequested = viewModel::forfeit
            )

            when (currentState) {
                MatchState.STARTING -> StartingMatchDialog()
                MatchState.FINISHED -> MatchEndedDialog(
                    localPLayerMarker = currentGame.localPlayer,
                    result = currentGame.getResult(),
                    onDismissRequested = { finish() }
                )

                else -> MatchState.STARTED
            }
        }

        if (viewModel.state == MatchState.IDLE) {
            viewModel.startMatch(localPlayer, challenge)
        }

        onBackPressedDispatcher.addCallback(this) {
            confirmForfeit()
        }
    }

    private val matchInfo: MatchInfo by lazy {
        intent.getParcelableExtraCompat<MatchInfo>(MATCH_INFO_EXTRA)
            ?: error("MatchInfo is missing")
    }

    private val localPlayer: PlayerInfo by lazy {
        PlayerInfo(
            info = UserInfo(matchInfo.localPlayerNick),
            id = matchInfo.localPlayerId
        )
    }

    private val challenge: Challenge by lazy {
        val opponent = PlayerInfo(
            info = UserInfo(matchInfo.opponentNick),
            id = matchInfo.opponentId
        )
        if (localPlayer.id == matchInfo.challengerId) {
            Challenge(challenger = localPlayer, challenged = opponent)
        } else {
            Challenge(challenger = opponent, challenged = localPlayer)
        }
    }

    private fun confirmForfeit() {
        viewModel.forfeit()
        finish()
    }

    private inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String): T {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableExtra(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableExtra<T>(key)
        } ?: throw IllegalStateException("$key is required but missing in the intent.")
    }
}

@Parcelize
internal data class MatchInfo(
    val localPlayerId: String,
    val localPlayerNick: String,
    val opponentId: String,
    val opponentNick: String,
    val challengerId: String
) : Parcelable

internal fun MatchInfo(localPlayer: PlayerInfo, challenge: Challenge): MatchInfo {
    val opponent = if (localPlayer == challenge.challenged) {
        challenge.challenger
    } else {
        challenge.challenged
    }
    return MatchInfo(
        localPlayerId = localPlayer.id,
        localPlayerNick = localPlayer.info.nickname,
        opponentId = opponent.id,
        opponentNick = opponent.info.nickname,
        challengerId = challenge.challenger.id
    )
}
