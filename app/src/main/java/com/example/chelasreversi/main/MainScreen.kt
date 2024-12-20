package com.example.chelasreversi.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chelasreversi.ui.theme.ChelasReversiTheme
import com.example.chelasreversi.R

const val MainScreenTag = "MainScreen"
const val PlayButtonTag = "PlayButton"

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit = { },
    onAuthorsClick: () -> Unit = { },
    onFavoriteGamesClick: () -> Unit = { }
) {
    ChelasReversiTheme {
        Surface(
            modifier = Modifier.fillMaxSize().testTag(MainScreenTag),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .paddingFromBaseline(top = 130.dp),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
                Image(
                    painter = painterResource(id = R.drawable.reversi),
                    contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 100.dp)
                        .size(350.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { onPlayClick() },
                        modifier = Modifier
                            .testTag(PlayButtonTag)
                            .padding(bottom = 20.dp)
                            .sizeIn(minWidth = 250.dp, minHeight = 60.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.start_game_button_text),
                            modifier = Modifier
                                .padding(10.dp),
                            fontSize = 25.sp,
                        )
                    }
                    Button(
                        onClick = { onAuthorsClick() },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .sizeIn(minWidth = 250.dp, minHeight = 60.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.main_menu_authors),
                            fontSize = 25.sp
                        )
                    }
                    Button(
                        onClick = { onFavoriteGamesClick() },
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .sizeIn(minWidth = 250.dp, minHeight = 60.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.favorite_games_button_text),
                            fontSize = 25.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}