import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.game.GameSummary

@Composable
fun FavoriteGamesScreen(
    favoriteGames: List<GameSummary>,
    onGameSelected: (GameSummary) -> Unit,
    onBackRequested: () -> Unit
) {
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
                    text = "Favorite Games",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 80.dp)
                )

                favoriteGames.forEach { gameSummary ->
                    Button(
                        onClick = { onGameSelected(gameSummary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 8.dp)
                            .height(60.dp)
                    ) {
                        Text(text = gameSummary.title)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    onClick = onBackRequested,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Back", fontSize = 20.sp)
                }
            }
        }
    )
}
