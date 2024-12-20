package com.example.chelasreversi.game.lobby.ui

import PlayerInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.CheRApplication
import com.example.chelasreversi.DependenciesContainer
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.game.lobby.domain.Challenge
import com.example.chelasreversi.game.play.ui.GameActivity
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.preferences.ui.PreferencesActivity
import com.example.chelasreversi.utils.viewModelInit
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LobbyActivity : ComponentActivity() {

    private val db by lazy {
        (application as CheRApplication).run {
            if (BuildConfig.DEBUG) emulatedDb else realDb
        }
    }

    private val viewModel by viewModels<LobbyScreenViewModel> {
        viewModelInit {
            val app = (application as DependenciesContainer)
            LobbyScreenViewModel(app.lobby, app.userInfoRepo)
        }
    }

    private val localPlayer: PlayerInfo by lazy {
        val app = application as DependenciesContainer
        val userInfo = app.userInfoRepo.userInfo
        PlayerInfo(
            info = userInfo!!,
            id = ""
        )
    }

    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, LobbyActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val players by viewModel.players.collectAsState()

            LobbyScreen(
                state = LobbyScreenState(players),
                onPlayerSelected = { selectedPlayer ->
                    val challenge = Challenge(
                        challenger = localPlayer,
                        challenged = selectedPlayer
                    )
                    viewModel.sendChallenge(selectedPlayer)
                    GameActivity.navigate(this, localPlayer, challenge)
                },
                onBackRequested = { onBackPressedDispatcher.onBackPressed() },
                onPreferencesRequested = {
                    PreferencesActivity.navigate(this, finishOnSave = true)
                },
                db = db
            )
        }
    }
}

@Composable
fun LobbyScreen(
    state: LobbyScreenState,
    onPlayerSelected: (PlayerInfo) -> Unit,
    onBackRequested: () -> Unit,
    onPreferencesRequested: () -> Unit,
    db: FirebaseFirestore,
) {
    var users by remember { mutableStateOf<List<PlayerInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    fun fetchPlayers(context: Context) {
        isLoading = true
        db.collection("users")
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                val userList = result.map { document ->
                    val nickname = document.getString("nickname") ?: ""
                    val userInfo = UserInfo(nickname)
                    PlayerInfo(userInfo, "")
                }
                users = userList
                isLoading = false

                val intent = Intent(context, PlayerListActivity::class.java)
                intent.putParcelableArrayListExtra("players", ArrayList(users))
                context.startActivity(intent)
            }
            .addOnFailureListener { exception: Exception ->
                Log.w("LobbyActivity", "Error getting documents: ", exception)
                isLoading = false
            }
    }

    val context = LocalContext.current
    Scaffold(
        topBar = { TopBar(navigationHandlers = NavigationHandlers(onBackHandler = onBackRequested)) },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Lobby",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 80.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { fetchPlayers(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(60.dp)
                    ) {
                        Text(
                            text = "Fetch Players",
                            fontSize = 20.sp
                        )
                    }

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                    }
                    users.forEach { playerInfo ->
                        Text(
                            text = playerInfo.info.nickname,
                            fontSize = 23.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .clickable { onPlayerSelected(playerInfo) }
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                    Button(
                        onClick = onPreferencesRequested,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .height(60.dp)
                    ) {
                        Text(text = "Preferences", fontSize = 20.sp)
                    }
                }
            }
        }
    )
}