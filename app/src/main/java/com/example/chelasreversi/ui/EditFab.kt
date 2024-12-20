package com.example.chelasreversi.ui

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

enum class FabMode { Edit, Save }

const val EditButtonTag = "EditButton"
const val SaveButtonTag = "SaveButton"

@Composable
fun EditFab(
    onClick: (() -> Unit)? = null,
    mode: FabMode = FabMode.Edit,
) {
    Button(
        onClick = onClick ?: { },
        enabled = if (mode == FabMode.Edit) true else onClick != null,
        shape = CircleShape,
        modifier = Modifier
            .defaultMinSize(minWidth = 56.dp, minHeight = 56.dp)
            .testTag(
                tag =
                if (mode == FabMode.Edit) EditButtonTag
                else SaveButtonTag
            )
    ) {
        if (mode == FabMode.Edit) {
            Icon(Icons.Default.Edit, contentDescription = "")
        } else {
            Icon(Icons.Default.Favorite, contentDescription = "")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditFabEditModePreview() {
    ChelasReversiTheme {
        EditFab(onClick = { }, mode = FabMode.Edit)
    }
}

@Preview(showBackground = true)
@Composable
private fun EditFabUpdateModePreview() {
    ChelasReversiTheme {
        EditFab(onClick = { }, mode = FabMode.Save)
    }
}

@Preview(showBackground = true)
@Composable
private fun DisabledEditFabUpdateModePreview() {
    ChelasReversiTheme {
        EditFab(mode = FabMode.Save)
    }
}