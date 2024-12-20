package com.example.chelasreversi.game.lobby.ui

import PlayerInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.R
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

//const val LobbyScreenTag = "LobbyScreen"

data class LobbyScreenState(
    val players: List<PlayerInfo> = emptyList()
)

@Preview(showBackground = true)
@Composable
fun LobbyScreen(
    state: LobbyScreenState = LobbyScreenState(),
    onPlayerSelected: (PlayerInfo) -> Unit = { },
    onBackRequested: () -> Unit = { },
    onPreferencesRequested: () -> Unit = { },

    ) {
    ChelasReversiTheme {
        Scaffold(
            topBar = { TopBar(navigationHandlers = NavigationHandlers(onBackHandler = onBackRequested)) },
            content = { innerPadding ->
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                ) {
                    Spacer(modifier = Modifier.height(55.dp))
                    Text(
                        text = stringResource(id = R.string.lobby_title),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Available Players",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        items(state.players) { player ->
                            PlayerInfoView(playerInfo = player, onPlayerSelected)
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LobbyScreenPreview() {
    LobbyScreen(
        state = LobbyScreenState(players),
        onBackRequested = { },
        onPreferencesRequested = { }
    )
}

private val players = buildList {
    repeat(30) {
        add(PlayerInfo(UserInfo("My Nickname $it"), it.toString()))
    }
}