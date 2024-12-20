package com.example.chelasreversi.game.lobby.ui

import PlayerInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.ui.GameActivity
import com.example.chelasreversi.preferences.domain.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PlayerListActivity : ComponentActivity() {

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var users by mutableStateOf<List<PlayerInfo>>(emptyList())

        fetchPlayers { fetchedUsers ->
            users = fetchedUsers
            setContent {
                PlayerListScreen(
                    players = users,
                    backHandle = { onBackPressedDispatcher.onBackPressed() },
                    onPlayerSelected = { selectedPlayer ->
                        val localPlayer = PlayerInfo(UserInfo("constancacastro"), "50523")
                        val challenge = Challenge(localPlayer, selectedPlayer)
                        GameActivity.navigate(this, localPlayer, challenge)
                    }

                )
            }
        }
    }

    private fun fetchPlayers(onPlayersFetched: (List<PlayerInfo>) -> Unit) {
        var isLoading by mutableStateOf(false)

        isLoading = true
        db.collection("users")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val userList = result.map { document ->
                    val nickname = document.getString("nickname") ?: ""
                    val userInfo = UserInfo(nickname)
                    PlayerInfo(userInfo, "")
                }
                isLoading = false
                onPlayersFetched(userList)
            }
            .addOnFailureListener { exception: Exception ->
                Log.w("PlayerListActivity", "Error getting documents: ", exception)
                isLoading = false
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListScreen(
    players: List<PlayerInfo>,
    backHandle: () -> Unit,
    onPlayerSelected: (PlayerInfo) -> Unit
) {
    val playerCount = players.size

    Scaffold(
        topBar = {
            TopBar(navigationHandlers = NavigationHandlers(onBackHandler = backHandle))
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .padding(top = 65.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Players available in the Lobby: $playerCount",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Spacer(modifier = Modifier.height(90.dp))

                players.forEach { playerInfo ->
                    Text(
                        text = playerInfo.info.nickname,
                        fontSize = 23.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { onPlayerSelected(playerInfo) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}
