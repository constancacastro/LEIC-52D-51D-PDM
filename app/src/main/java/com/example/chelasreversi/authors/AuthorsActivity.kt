package com.example.chelasreversi.authors

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.chelasreversi.R
import com.example.chelasreversi.ui.theme.ChelasReversiTheme

class AuthorsScreenActivity : ComponentActivity() {

    /* companion object {
        fun navigate(source: Context) {
            val intent = Intent(source, AuthorsScreenActivity::class.java)
            source.startActivity(intent)
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChelasReversiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthorsScreen(
                        backHandle = { this.finish() },
                        sendEmail = {
                            sendEmailIntent(
                                it,
                                "",
                                "Parabéns pelo excelente trabalho. 😝"
                            )
                        }
                    )
                }
            }
        }
    }

    private fun sendEmailIntent(email: String, subject: String, content: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, content)
            }
            startActivity(Intent.createChooser(intent, "Choose an email client"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                getString(R.string.activity_info_no_suitable_app),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
