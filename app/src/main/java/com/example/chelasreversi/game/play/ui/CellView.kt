package com.example.chelasreversi.game.play.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.R
import com.example.chelasreversi.game.play.domain.Player
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

internal const val CellViewTAG = "CellView"

@Composable
fun CellView(
    move: Player?,
    enabled: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize(1.0f)
        .padding(0.dp)
        .testTag(CellViewTAG)
        .clickable(enabled = move == null && enabled) { onSelected() }
    ) {
        if (move != null) {
            val player = when (move) {
                Player.BLACK -> R.drawable.black_piece
                Player.WHITE -> R.drawable.white_piece
            }
            Image(
                painter = painterResource(id = player),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BlackPiecePreview() {
    ChelasReversiTheme {
        CellView(move = Player.BLACK, enabled = true, onSelected = { })
    }
}


@Preview(showBackground = true)
@Composable
private fun WhitePiecePreview() {
    ChelasReversiTheme {
        CellView(move = Player.WHITE, enabled = true, onSelected = { })
    }
}

@Preview(showBackground = true)
@Composable
private fun CellEmptyPreview() {
    ChelasReversiTheme {
        CellView(move = null, enabled = true, onSelected = { })
    }
}