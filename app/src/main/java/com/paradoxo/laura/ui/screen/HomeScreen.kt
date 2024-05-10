package com.paradoxo.laura.ui.screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meetup.twain.MarkdownText
import com.paradoxo.laura.R
import com.paradoxo.laura.model.Message
import com.paradoxo.laura.model.Status
import com.paradoxo.laura.ui.components.Loader
import com.paradoxo.laura.ui.theme.LauraTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    chatHistory: List<Message> = emptyList(),
    onSendPrompt: (String, List<Bitmap>) -> Unit = { _, _ -> },
    onShowSettings: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    var value by remember {
        mutableStateOf("")
    }

    val contentResolver = LocalContext.current.contentResolver

    val imageBitmaps = remember { mutableStateListOf<Bitmap>() }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { imageUri ->
        imageUri?.let {
            val bitmap = contentResolver?.let { contentResolver ->
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, it)
                }
            }
            bitmap?.let { bitmapNew -> imageBitmaps.add(bitmapNew) }
        }
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

                        Column(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp, 8.dp)
                        ) {


                            MarkdownText(
                                message.text,
                                color = Color.White,
                            )

                            if (message.imagesList.isNotEmpty()) {
                                message.imagesList.forEach {
                                    AsyncImage(
                                        it,
                                        contentDescription = "Imagem",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(4.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
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

        if (imageBitmaps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .sizeIn(minHeight = 56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageBitmaps.forEach { bitmap ->
                    Image(
                        bitmap.asImageBitmap(),
                        contentDescription = "Imagem",
                        modifier = Modifier
                            .clickable {
                                imageBitmaps.remove(bitmap)
                            }
                            .size(56.dp)
                            .padding(4.dp),
                        contentScale = ContentScale.Crop
                    )
                }
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
                        onSendPrompt(value, imageBitmaps)
                        value = ""
                        focusManager.clearFocus()
                    })
            )


            IconButton(onClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
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
                onSendPrompt(value, imageBitmaps)
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
fun HomeScreenPreview() {
    LauraTheme {
        HomeScreen()
    }
}
