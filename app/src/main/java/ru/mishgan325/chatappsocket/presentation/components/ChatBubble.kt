import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.mishgan325.chatappsocket.models.Message

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor =
        if (message.isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
    val textColor =
        if (message.isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
    val alignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart

    val formattedTime = formatTimestampToTime(message.timestamp)
    val context = LocalContext.current

    var isImagePreviewOpen by remember { mutableStateOf(false) }

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
                    modifier = Modifier.padding(12.dp)
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
                        (message.fileUrl.endsWith(".jpg", true) ||
                                message.fileUrl.endsWith(".png", true) ||
                                message.fileUrl.endsWith(".jpeg", true) ||
                                message.fileUrl.endsWith(".gif", true))
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
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📥 Скачать файл",
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(
                                fontSize = 14.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(message.fileUrl)
                                }
                                context.startActivity(intent)
                            }
                        )
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

    if (isImagePreviewOpen) {
        Dialog(onDismissRequest = { isImagePreviewOpen = false }) {
            // Зум и пан
            var scale by remember { mutableStateOf(1f) }
            var offsetX by remember { mutableStateOf(0f) }
            var offsetY by remember { mutableStateOf(0f) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    }
                    .clickable { isImagePreviewOpen = false }, // Закрытие по нажатию
                contentAlignment = Alignment.Center
            ) {
                // Прозрачный фон
                Surface(
                    color = Color.Black.copy(alpha = 0.8f), // Можно сделать 0.6f, если хочешь "размытый" эффект
                    tonalElevation = 0.dp,
                    shape = MaterialTheme.shapes.medium
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
}
