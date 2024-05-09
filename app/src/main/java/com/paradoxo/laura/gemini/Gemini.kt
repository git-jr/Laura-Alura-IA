package com.paradoxo.laura.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content

class Gemini(
    private val apiKey: String = "",
) {

    private val defaultInstruction =
        "Responda sabendo que você é: uma inteligência artificial chamada 'Laura' que auxilia estudantes na Alura, o maior ecossistema de ensino de tecnologia do Brasil e que sua irmã é se chama 'Luri'. Não precisa incluir essa informação na resposta."
    private lateinit var generativeModel: GenerativeModel
    private lateinit var chat: Chat

    init {
        setupModel()
    }

    fun setupModel(
        modelName: String = "gemini-pro-vision",
        chatMode: Boolean = false
    ) {
        generativeModel = GenerativeModel(
            modelName = modelName,
            apiKey = apiKey
        )

        if (chatMode) {
            chat = generativeModel.startChat()
        }
    }

    suspend fun sendPrompt(
        imageList: List<Bitmap>,
        prompt: String
    ) {
        val inputContent: Content = content {
            imageList.forEach {
                image(it)
            }
            text(prompt)
            text(defaultInstruction)
        }

        val response = generativeModel.generateContent(inputContent)
        print(response.text)
    }

    suspend fun sendPromptChat(
        prompt: String,
        imageList: List<Bitmap> = emptyList(),
        onResponse: (String) -> Unit = {}
    ) {
        val inputContent: Content = content {
            imageList.forEach {
                image(it)
            }
            text(prompt)
            text(defaultInstruction)
        }

        chat.sendMessage(inputContent).let { response ->
            print(response.text)
            response.text?.let {
                onResponse(it)
            }
        }

//        chat.sendMessageStream(inputContent).collect { chunk ->
//            print(chunk.text)
//            chunk.text?.let { text ->
//                onResponse(text)
//            }
//        }
    }

}