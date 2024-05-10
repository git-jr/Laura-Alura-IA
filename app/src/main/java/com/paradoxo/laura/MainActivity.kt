package com.paradoxo.laura

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.paradoxo.laura.gemini.Gemini
import com.paradoxo.laura.model.Message
import com.paradoxo.laura.model.Status
import com.paradoxo.laura.ui.screen.HomeScreen
import com.paradoxo.laura.ui.screen.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val gemini = Gemini()
            gemini.setupModel(
                apiKey = "",
                modelName = "gemini-pro"
            )

            var chatHistory by remember { mutableStateOf(listOf<Message>()) }
            var showSetting by remember { mutableStateOf(true) }

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                val scope = rememberCoroutineScope()

                if (!showSetting) {
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        chatHistory = chatHistory.reversed(),
                        onSendPrompt = { prompt, images ->
                            chatHistory.toMutableList().apply {
                                add(
                                    Message(
                                        text = prompt,
                                        status = Status.USER,
                                        imagesList = images
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
                                    imageList = images
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

