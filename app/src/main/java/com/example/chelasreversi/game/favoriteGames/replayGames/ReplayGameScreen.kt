package com.example.chelasreversi.game.favoriteGames.replayGames

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.chelasreversi.game.play.ui.ReversiBoardView


@Composable
fun ReplayGameScreen(
    viewModel: ReplayGameViewModel,
    onExit: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Display the game board
        ReversiBoardView(
            board = viewModel.currentBoard,
            onCellSelected = {},
            enabled = false,
            modifier = Modifier.weight(1f)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = viewModel::goToPrevious, enabled = viewModel.canGoToPrevious) {
                Text("Previous")
            }
            Button(onClick = onExit) {
                Text("Exit")
            }
            Button(onClick = viewModel::goToNext, enabled = viewModel.canGoToNext) {
                Text("Next")
            }
        }
    }
}

