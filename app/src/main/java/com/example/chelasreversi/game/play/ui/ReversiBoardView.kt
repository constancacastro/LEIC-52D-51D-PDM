package com.example.chelasreversi.game.play.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.game.play.domain.BOARD_SIDE
import com.example.chelasreversi.game.play.domain.Board
import com.example.chelasreversi.game.play.domain.Coordinates
import com.example.chelasreversi.game.play.domain.Player
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

@Composable
fun ReversiBoardView(
    board: Board,
    onCellSelected: (coordinates: Coordinates) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(BOARD_SIDE) { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(weight = 1.0f, fill = true)
                    .fillMaxWidth(),
            ) {
                repeat(BOARD_SIDE) { column ->
                    val coordinates = Coordinates(row, column)
                    CellView(
                        move = board[coordinates],
                        enabled = enabled,
                        modifier = Modifier
                            .weight(weight = 1.0f, fill = true),
                        onSelected = { onCellSelected(coordinates) },
                    )
                    if (column != BOARD_SIDE - 1) {
                        VerticalSeparator()
                    }
                }
            }
            if (row != BOARD_SIDE - 1) {
                HorizontalSeparator()
            }
        }
    }
}

@Composable
private fun VerticalSeparator() {
    Spacer(
        modifier = Modifier
            .fillMaxHeight()
            .width(2.dp)
            .background(MaterialTheme.colorScheme.secondary),
    )
}

@Composable
private fun HorizontalSeparator() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(MaterialTheme.colorScheme.secondary)
    )
}

@Preview(showBackground = true)
@Composable
private fun EmptyBoardViewPreview() {
    ChelasReversiTheme{
        ReversiBoardView(board = Board(), enabled = true, onCellSelected = { })
    }
}

@Preview(showBackground = true)
@Composable
private fun NonEmptyBoardViewPreview() {
    ChelasReversiTheme {
        ReversiBoardView(board = initialBoard, enabled = true, onCellSelected = { })
    }
}

private val initialBoard = Board(
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