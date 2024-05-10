package com.paradoxo.laura.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paradoxo.laura.R
import com.paradoxo.laura.ui.theme.LauraTheme

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
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var apiKeyState by remember { mutableStateOf(apiKey) }
        var defaultInstructionState by remember { mutableStateOf(defaultInstruction) }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.laura_square_logo),
                contentDescription = "Chat",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(150.dp),
            )

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

            Spacer(modifier = Modifier.height(16.dp))

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
}




@Preview
@Composable
private fun SettingsScreenPreview() {
    LauraTheme {
        SettingsScreen()
    }
}