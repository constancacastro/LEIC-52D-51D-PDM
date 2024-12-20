package com.example.chelasreversi.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.chelasreversi.R
import com.example.chelasreversi.ui.theme.ChelasReversiTheme


/**
 * Used to aggregate [TopBar] navigation handlers.
 */
data class NavigationHandlers(
    val onBackRequested: (() -> Unit)? = null,
    val onPreferencesRequested: (() -> Unit)? = null,
)

// Test tags for the TopBar navigation elements
const val NavigateBackTag = "NavigateBack"
const val NavigateToPreferencesTag = "NavigateToPreferences"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navigation: NavigationHandlers = NavigationHandlers()) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            if (navigation.onBackRequested != null) {
                IconButton(
                    onClick = navigation.onBackRequested,
                    modifier = Modifier.testTag(NavigateBackTag)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.top_bar_go_back)
                    )
                }
            }
        },
        actions = {
            if (navigation.onPreferencesRequested != null) {
                IconButton(
                    onClick = navigation.onPreferencesRequested,
                    modifier = Modifier.testTag(NavigateToPreferencesTag)
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.top_bar_navigate_to_prefs)
                    )
                }
            }
        }
    )}

@Preview
@Composable
private fun TopBarPreviewBack() {
    ChelasReversiTheme {
        TopBar(
            NavigationHandlers(onBackRequested = { })
        )
    }
}

@Preview
@Composable
private fun TopBarPreviewBackAndPrefs() {
    ChelasReversiTheme {
        TopBar(
            NavigationHandlers(onBackRequested = { }, onPreferencesRequested = { })
        )
    }
}

@Preview
@Composable
private fun TopBarPreviewNoNavigation() {
    ChelasReversiTheme {
        TopBar()
    }
}
