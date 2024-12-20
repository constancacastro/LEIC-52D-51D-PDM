package com.example.chelasreversi.preferences.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chelasreversi.R
import com.example.chelasreversi.authors.NavigationHandlers
import com.example.chelasreversi.authors.TopBar
import com.example.chelasreversi.preferences.domain.UserInfo
import com.example.chelasreversi.ui.EditFab
import com.example.chelasreversi.ui.FabMode
import com.example.chelasreversi.ui.IsReadOnly
import com.example.chelasreversi.ui.theme.ChelasReversiTheme


import kotlin.math.min

const val PreferencesScreenTag = "PreferencesScreen"
const val NicknameInputTag = "NicknameInput"

@Composable
fun PreferencesScreen(
    userInfo: UserInfo?,
    onBackRequested: () -> Unit = { },
    onSaveRequested: (UserInfo) -> Unit = { }
) {
    ChelasReversiTheme {
        var displayedNick by remember { mutableStateOf(userInfo?.nickname ?: "") }
        var editing by remember { mutableStateOf(userInfo == null) }

        val enteredInfo = UserInfo(
            nickname = displayedNick.trim(),
        )

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag(PreferencesScreenTag),
            topBar = { TopBar(NavigationHandlers(onBackRequested)) },
            floatingActionButton = {
                EditFab(
                    onClick =
                    if (!editing) {
                        { editing = true }
                    } else run {
                        { onSaveRequested(enteredInfo) }
                    },
                    mode = if (editing) FabMode.Save else FabMode.Edit
                )
            }
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.preferences_screen_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(70.dp))

                OutlinedTextField(
                    value = displayedNick,
                    onValueChange = { displayedNick = ensureInputBounds(it) },
                    singleLine = true,
                    label = {
                        Text(stringResource(id = R.string.preferences_screen_nickname_tip))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "")
                    },
                    readOnly = !editing,
                    modifier = Modifier
                        .padding(horizontal = 48.dp)
                        .fillMaxWidth()
                        .testTag(NicknameInputTag)
                        .semantics { if (!editing) this[IsReadOnly] = Unit }
                )
                Spacer(
                    modifier = Modifier
                        .sizeIn(minHeight = 128.dp, maxHeight = 256.dp)
                )
            }
        }
    }
}
private const val MAX_INPUT_SIZE = 50
private fun ensureInputBounds(input: String) =
    input.also {
        it.substring(range = 0 until min(it.length, MAX_INPUT_SIZE))
    }

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenViewModePreview() {
    PreferencesScreen(
        userInfo = UserInfo("consti"),
        onSaveRequested = { }
    )
}

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenEditModePreview() {
    PreferencesScreen(
        userInfo = null,
        onSaveRequested = { }
    )
}