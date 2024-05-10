package com.paradoxo.laura.gemini

import android.graphics.Bitmap
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content

class Gemini {
    var defaultInstruction =
        "Responda sabendo que você é: uma inteligência artificial chamada 'Laura' que auxilia estudantes na Alura, o maior ecossistema de ensino de tecnologia do Brasil e que sua irmã é se chama 'Luri'. Não precisa incluir essa informação na resposta."

    var apiKey: String = ""
    private var modelName: String = "gemini-pro-vision"

    private lateinit var generativeModel: GenerativeModel
    private lateinit var chat: Chat

    fun setupModel(
        apiKey: String,
        modelName: String = "gemini-pro-vision"
    ) {
        this.apiKey = apiKey
        this.modelName = modelName

        loadModel()

    }

    private fun loadModel() {
        generativeModel = GenerativeModel(
            modelName = this.modelName,
            apiKey = this.apiKey
        )
        chat = generativeModel.startChat()
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
    }


    fun redefineDefaultData(
        defaultInstruction: String? = "",
        apiKey: String? = ""
    ) {
        defaultInstruction?.let {
            this.defaultInstruction = it
        }

        apiKey?.let {
            this.apiKey = it
        }

        loadModel()
    }

}