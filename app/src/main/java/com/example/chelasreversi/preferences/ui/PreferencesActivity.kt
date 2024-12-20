package com.example.chelasreversi.preferences.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chelasreversi.DependenciesContainer
import com.example.chelasreversi.game.lobby.ui.LobbyActivity


const val FINISH_ON_SAVE_EXTRA = "FinishOnSaveExtra"

class PreferencesActivity : ComponentActivity() {

    private val repo by lazy {
        (application as DependenciesContainer).userInfoRepo
    }

    companion object {
        fun navigate(context: Context, finishOnSave: Boolean = false) {
            with(context) {
                val intent = Intent(this, PreferencesActivity::class.java)
                if (finishOnSave) {
                    intent.putExtra(FINISH_ON_SAVE_EXTRA, true)
                }
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferencesScreen(
                userInfo = repo.userInfo,
                onBackRequested = { finish() },
                onSaveRequested = {
                    repo.userInfo = it
                    if (intent.getBooleanExtra(FINISH_ON_SAVE_EXTRA, false)) {
                        finish()
                    } else {
                        LobbyActivity.navigate(this)
                    }
                }
            )
        }
    }
}