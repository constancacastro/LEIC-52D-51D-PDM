package com.example.chelasreversi.game.play.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.game.play.domain.Coordinates
import com.example.chelasreversi.game.play.domain.Game
import com.example.chelasreversi.ui.theme.ChelasReversiTheme
import com.example.chelasreversi.R
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Player

internal const val GameScreenTag = "GameScreen"
internal const val GameScreenTitleTag = "GameScreenTitle"
internal const val ForfeitButtonTag = "ForfeitButton"
val onBackHandler: () -> Unit = { }
data class GameScreenState(
    @StringRes val title: Int?,
    val game: Game
)

@Composable
fun GameScreen(
    state: GameScreenState,
    onMoveRequested: (Coordinates) -> Unit = { },
    onForfeitRequested: () -> Unit = { },
) {
    ChelasReversiTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(GameScreenTag),
            topBar = {
                TopBar(navigationHandlers = NavigationHandlers(onBackHandler = onBackHandler))
            },
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                val titleTextId = when {
                    state.title != null -> state.title
                    state.game.localPlayer == state.game.board.turn ->
                        R.string.game_screen_your_turn

                    else -> R.string.game_screen_opponent_turn
                }
                Text(
                    text = stringResource(id = titleTextId),
                    fontSize = 30.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .testTag(GameScreenTitleTag)
                        .padding(20.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(10F, true)
                        .fillMaxSize()
                        .aspectRatio(0.85F)
                        .border(6.dp, MaterialTheme.colorScheme.primary) // Add border here
                ) {
                    ReversiBoardView(
                        board = state.game.board,
                        onCellSelected = onMoveRequested,
                        enabled = state.game.localPlayer == state.game.board.turn,
                        modifier = Modifier
                            .padding(11.dp)
                            .fillMaxSize()
                            .aspectRatio(0.8F)
                    )
                }
                Button(
                    onClick = onForfeitRequested,
                    modifier = Modifier
                        .testTag(ForfeitButtonTag)
                        .width(130.dp)
                        .height(50.dp)
                        .border(3.dp, Color.Black, shape = CircleShape)
                ) {
                    Text(text = stringResource(id = R.string.game_screen_forfeit),
                        modifier = Modifier
                            .padding(4.dp),
                        fontSize = 16.sp,

                        )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    GameScreen(
        state = GameScreenState(
            title = null,
            Game(localPlayer = Player.WHITE, board = aBoard, gameId = "")
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenWaiting() {
    GameScreen(state = GameScreenState(
            title = R.string.game_screen_waiting,
            Game(localPlayer = Player.WHITE, board = Board(Player.WHITE), gameId = "")
        )
    )
}

private val aBoard = Board(
    turn = Player.BLACK,
    cell = List(8) { row ->
        List(8) { col ->
            when {
                row == 3 && col == 3 -> Player.WHITE
                row == 3 && col == 4 -> Player.BLACK
                row == 4 && col == 3 -> Player.BLACK
                row == 4 && col == 4 -> Player.WHITE
                else -> null
            }
        }
    }
)