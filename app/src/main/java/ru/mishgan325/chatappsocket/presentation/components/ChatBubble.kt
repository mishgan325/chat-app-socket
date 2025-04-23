import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.URL
import ru.mishgan325.chatappsocket.domain.models.Message

@Composable
fun ChatBubble(
    message: Message,
    onEditMessage: (Long, String) -> Unit,
    onDeleteMessage: (Long) -> Unit
) {
    val bubbleColor =
        if (message.isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor =
        if (message.isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val alignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart

    val formattedTime = formatTimestampToTime(message.timestamp)
    val context = LocalContext.current

    var isImagePreviewOpen by remember { mutableStateOf(false) }
    var pendingDownloadUrl by remember { mutableStateOf<String?>(null) }

    var showEditDeleteDialog by remember { mutableStateOf(false) }
    var editedMessageContent by remember { mutableStateOf(message.content) }

    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        if (uri != null && pendingDownloadUrl != null) {
            coroutineScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        val input = URL(pendingDownloadUrl).openStream()
                        val output: OutputStream? = context.contentResolver.openOutputStream(uri)
                        input.use { inputStream ->
                            output?.use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace() // или покажи snackbar/Toast
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = bubbleColor,
                tonalElevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                if (message.isMine) { // Только свои сообщения
                                    showEditDeleteDialog = true
                                }
                            }
                        )
                    },
                ) {
                    if (!message.isMine) {
                        Text(
                            text = message.sender.username,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.content,
                        color = textColor,
                        style = TextStyle(fontSize = 16.sp)
                    )

                    // Превью изображения с кликом
                    if (message.fileUrl.isNotBlank() &&
                        (message.fileUrl.contains(".jpg", true) ||
                                message.fileUrl.contains(".png", true) ||
                                message.fileUrl.contains(".jpeg", true) ||
                                message.fileUrl.contains(".gif", true))
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(message.fileUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Attached image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable { isImagePreviewOpen = true }
                        )
                    }

                    // Кнопка "Скачать файл"
                    if (message.fileUrl.isNotBlank()) {
                        val fileName = Uri.parse(message.fileUrl)
                            .lastPathSegment
                            ?.substringAfter("-")
                            ?.substringBefore("?")
                            ?: "Файл"

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                            tonalElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pendingDownloadUrl = message.fileUrl
                                    launcher.launch(fileName)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "File",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = fileName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.primary
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = formattedTime,
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }

    // Диалог для редактирования и удаления
    if (showEditDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showEditDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onEditMessage(message.id, editedMessageContent)
                    showEditDeleteDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDeleteMessage(message.id)
                    showEditDeleteDialog = false
                }) {
                    Text("Удалить")
                }
            },
            title = { Text("Редактировать сообщение") },
            text = {
                OutlinedTextField(
                    value = editedMessageContent,
                    onValueChange = { editedMessageContent = it },
                    label = { Text("Сообщение") },
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            textContentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    if (isImagePreviewOpen) {
        Dialog(
            onDismissRequest = { isImagePreviewOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            // Зум и пан
            var scale by remember { mutableStateOf(1f) }
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                    .clickable { isImagePreviewOpen = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = message.fileUrl,
                    contentDescription = "Full Image",
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                        .padding(16.dp)
                )
            }
        }
    }
}