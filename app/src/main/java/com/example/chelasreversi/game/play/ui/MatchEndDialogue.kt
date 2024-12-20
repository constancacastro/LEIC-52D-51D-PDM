package com.example.chelasreversi.game.play.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.R
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Player
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

const val MatchEndedDialogTag = "MatchEndedDialog"
const val MatchEndedDialogDismissButtonTag = "MatchEndedDialogDismissButton"

@Composable
fun MatchEndedDialog(
    localPLayerMarker: Player,
    result: Board.BoardResult,
    onDismissRequested: () -> Unit = { }
) {
    val dialogTextId = when {
        result is Board.Tied -> R.string.match_ended_dialog_text_tied_match
        result is Board.HasWinner && result.winner == localPLayerMarker -> R.string.match_ended_dialog_text_local_won
        else -> R.string.match_ended_dialog_text_opponent_won
    }

    AlertDialog(
        onDismissRequest = onDismissRequested,
        confirmButton = {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                OutlinedButton(
                    border = BorderStroke(0.dp, Color.Unspecified),
                    onClick = onDismissRequested,
                    modifier = Modifier.testTag(MatchEndedDialogDismissButtonTag)
                ) {
                    Text(
                        text = stringResource(id = R.string.match_ended_ok_button),
                        fontSize = 14.sp
                    )
                }
            }
        },
        title = { Text(stringResource(id = R.string.match_ended_dialog_title)) },
        text = { Text(stringResource(id = dialogTextId)) },
        modifier = Modifier.testTag(MatchEndedDialogTag)
    )
}

@Preview(showBackground = true)
@Composable
private fun MatchEndedDialogTiedPreview() {
    ChelasReversiTheme {
        MatchEndedDialog(
            localPLayerMarker = Player.WHITE,
            result = Board.Tied()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchEndedDialogLocalWonPreview() {
    ChelasReversiTheme {
        MatchEndedDialog(
            localPLayerMarker = Player.WHITE,
            result = Board.HasWinner(winner = Player.WHITE)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchEndedDialogOpponentWonPreview() {
    ChelasReversiTheme {
        MatchEndedDialog(
            localPLayerMarker = Player.WHITE,
            result = Board.HasWinner(winner = Player.BLACK)
        )
    }
}