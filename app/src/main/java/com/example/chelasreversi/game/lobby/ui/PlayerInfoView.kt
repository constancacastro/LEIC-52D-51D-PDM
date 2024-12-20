package com.example.chelasreversi.game.lobby.ui

import PlayerInfo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

const val PlayerInfoViewTag = "PlayerInfoView"

@Composable
fun PlayerInfoView(
    playerInfo: PlayerInfo,
    onPlayerSelected: (PlayerInfo) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayerSelected(playerInfo) }
            .testTag(PlayerInfoViewTag)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = playerInfo.info.nickname,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Start,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UserInfoViewPreview() {
    ChelasReversiTheme {
        PlayerInfoView(
            playerInfo = PlayerInfo(UserInfo("consti"), ""),
            onPlayerSelected = { }
        )
    }
}
