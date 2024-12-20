package com.example.chelasreversi.authors

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavigationHandlers(val onBackHandler: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navigationHandlers: NavigationHandlers) {
    TopAppBar(
        title = { Text("Go Back", fontSize = 22.sp) },
        navigationIcon = {
            IconButton(onClick = navigationHandlers.onBackHandler) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun AuthorsScreen(
    backHandle: () -> Unit,
    sendEmail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(topBar = { TopBar(navigationHandlers = NavigationHandlers(onBackHandler = backHandle)) }) { pad ->
        Column(
            modifier = modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 1.dp, vertical = 50.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Column {
                Text(
                    text = "Thank you for trying our game 🥳",
                    fontSize = 23.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "If you have any suggestions, please contact us!",
                    color = Color.DarkGray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(15.dp)
                )
            }
        }
        Column(
            modifier = modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 1.dp, vertical = 150.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
                    .padding(top = 30.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "50523 - Constança Castro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
                Text(
                    text = "A50523@alunos.isel.pt",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "50480 - Guilherme Mouzinho",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
                Text(
                    text = "A50480@alunos.isel.pt",
                    fontSize = 16.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
            ) {
                SendEmail(
                    sendEmail = sendEmail,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun SendEmail(sendEmail: (String) -> Unit, modifier: Modifier) {
    Button(
        onClick = { sendEmail("A50523@alunos.isel.pt,A50480@alunos.isel.pt") },
        modifier = modifier
            .padding(16.dp)
            .size(150.dp, 50.dp)
    ) {
        Text(
            text = "Send Email",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSendEmail() {
    SendEmail(sendEmail = {}, modifier = Modifier)
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthorsScreen() {
    AuthorsScreen(backHandle = {}, sendEmail = {})
}