package com.paradoxo.laura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.paradoxo.laura.gemini.Gemini
import com.paradoxo.laura.model.Message
import com.paradoxo.laura.model.Status
import com.paradoxo.laura.ui.theme.LauraTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            val gemini = Gemini()
            gemini.setupModel(
                apiKey = "",
                modelName = "gemini-pro"
            )

            var chatHistory by remember { mutableStateOf(listOf<Message>()) }

            var showSetting by remember { mutableStateOf(true) }

            LauraTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (!showSetting) {
                        MainCard(
                            modifier = Modifier.padding(innerPadding),
                            chatHistory = chatHistory.reversed(),
                            onSendPrompt = { prompt ->
                                chatHistory.toMutableList().apply {
                                    add(
                                        Message(
                                            text = prompt,
                                            status = Status.USER
                                        )
                                    )
                                    add(
                                        Message(status = Status.LOAD)
                                    )
                                }.also {
                                    chatHistory = it
                                }

                                scope.launch {
                                    gemini.sendPromptChat(
                                        prompt = prompt,
                                    ) { text ->
                                        chatHistory.toMutableList().apply {
                                            add(
                                                Message(
                                                    text = text,
                                                    status = Status.AI
                                                )
                                            )
                                        }.also {
                                            chatHistory = it.filter { it.status != Status.LOAD }
                                        }
                                    }
                                }
                            },
                            onShowSettings = {
                                showSetting = true
                            }
                        )
                    } else {
                        SettingsScreen(
                            apiKey = gemini.apiKey,
                            defaultInstruction = gemini.defaultInstruction,
                            onSave = { apiKey, defaultInstruction ->
                                showSetting = false
                                gemini.redefineDefaultData(
                                    apiKey = apiKey,
                                    defaultInstruction = defaultInstruction
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    chatHistory: List<Message> = emptyList(),
    onSendPrompt: (String) -> Unit = {},
    onShowSettings: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    var value by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .background(Color(0xFF00162C))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 56.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(onClick = {
                onShowSettings()
            }) {
                Icon(
                    Icons.Default.Settings,
                    "Enviar",
                    tint = Color(0xFF2BFCBD),
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        }


        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(8f),
            reverseLayout = true
        ) {

            items(chatHistory) { message ->
                if (message.status != Status.LOAD) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = if (Status.AI == message.status) Arrangement.Start else Arrangement.End
                    ) {
                        if (Status.USER == message.status) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp, 8.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = Color.White,
                            )
                        }

                        if (Status.AI == message.status) {
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                } else {
                    Loader()
                }
            }

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
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .sizeIn(minHeight = 56.dp)
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


@Composable
fun Loader() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.Asset("loading.json"),
        )
        LottieAnimation(
            composition,
            isPlaying = true,
            iterations = Int.MAX_VALUE
        )
    }
}


@Composable
fun SettingsScreen(
    apiKey: String = "",
    defaultInstruction: String = "",
    onSave: (
        apiKey: String,
        defaultInstruction: String
    ) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00162C)),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var apiKeyState by remember { mutableStateOf(apiKey) }
        var defaultInstructionState by remember { mutableStateOf(defaultInstruction) }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configurações",
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = apiKeyState,
                onValueChange = { apiKeyState = it },
                label = {
                    Text(
                        "API Key",
                        color = Color.White
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
            )

            OutlinedTextField(
                value = defaultInstructionState,
                onValueChange = { defaultInstructionState = it },
                label = {
                    Text(
                        "Instrução Padrão",
                        color = Color.White
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                minLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    unfocusedTextColor = Color.White,
                )
            )
        }

        // botão de salvar
        Box(
            modifier = Modifier
                .clickable { onSave(apiKeyState, defaultInstructionState) }
                .height(56.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color(0xFF2BFCBD), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Salvar",
                color = Color(0xFF00162C),
                fontSize = 16.sp
            )
        }
    }
}


@Preview
@Composable
private fun SettingsScreenPreview() {
    LauraTheme {
        SettingsScreen()
    }
}