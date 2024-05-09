package com.paradoxo.laura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paradoxo.laura.gemini.Gemini
import com.paradoxo.laura.ui.theme.LauraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()

            val gemini2 = Gemini()
            gemini2.setupModel(
                modelName = "gemini-pro",
                chatMode = true,
            )

            var chatHistory by remember {
                mutableStateOf(listOf<String>())
            }

            LauraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    MainCard(
                        modifier = Modifier.padding(innerPadding),
                        chatHistory = chatHistory,
                        onSendPrompt = { prompt ->
                            chatHistory.toMutableList().apply {
                                add(prompt)
                            }.also {
                                chatHistory = it
                            }
                            scope.launch {
                                gemini2.sendPromptChat(
                                    prompt = prompt,
                                ) { text ->
                                    chatHistory.toMutableList().apply {
                                        add(text)
                                    }.also {
                                        chatHistory = it
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    chatHistory: List<String> = emptyList(),
    onSendPrompt: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    var value by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .background(Color(0xFF00162C))
    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(8f)
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.laura_banner_transparent),
                    contentDescription = "Chat",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            items(chatHistory) { message ->
                Text(
                    text = message,
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .height(56.dp)
                .background(Color(0xFF2BFCBD)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                placeholder = {
                    Text(
                        text = "Digite uma mensagem...",
                        color = Color(0xFF00162C)
                    )
                },
                value = value,
                onValueChange = {
                    value = it
                },
                modifier = Modifier
                    .weight(5f)
                    .background(color = Color.Transparent),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSendPrompt(value)
                        value = ""
                        focusManager.clearFocus()
                    })
            )


            IconButton(onClick = {
            }) {
                Icon(
                    Icons.Filled.AddCircle,
                    "Documento",
                    tint = Color(0xFF00162C),
                    modifier = Modifier
                        .weight(1f)
                )
            }

            IconButton(onClick = {
                onSendPrompt(value)
                value = ""
                focusManager.clearFocus()
            }) {
                Icon(
                    Icons.Filled.Send,
                    "Enviar",
                    tint = Color(0xFF00162C),
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun MainCardPreview() {
    LauraTheme {
        MainCard()
    }
}
