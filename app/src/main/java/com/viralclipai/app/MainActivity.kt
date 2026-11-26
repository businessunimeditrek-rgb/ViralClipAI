package com.viralclipai.app

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File
import java.io.FileInputStream

private val Bg = Color(0xFF090B16)
private val Panel = Color(0xFF151A2B)
private val Accent = Color(0xFF8B5CF6)
private val Muted = Color(0xFFADB5CC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ViralClipApp() }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ViralClipApp() {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var url by remember { mutableStateOf("") }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var videoDurationMs by remember { mutableLongStateOf(0L) }
    var startSeconds by remember { mutableStateOf("0") }
    var endSeconds by remember { mutableStateOf("30") }
    var message by remember { mutableStateOf("Choose a video from your phone to begin.") }
    var isExporting by remember { mutableStateOf(false) }
    var pendingOutput by remember { mutableStateOf<File?>(null) }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("video/mp4")
    ) { destination ->
        val file = pendingOutput
        if (destination != null && file != null) {
            message = "Saving MP4 to your selected location…"
            Thread {
                val result = runCatching {
                    context.contentResolver.openOutputStream(destination)?.use { output ->
                        FileInputStream(file).use { input -> input.copyTo(output) }
                    } ?: error("Could not open the selected save location.")
                }
                Handler(Looper.getMainLooper()).post {
                    message = if (result.isSuccess) {
                        "Success! Your vertical MP4 has been saved."
                    } else {
                        "Could not save the MP4: ${result.exceptionOrNull()?.message ?: "unknown error"}"
                    }
                    pendingOutput = null
                    file.delete()
                }
            }.start()
        } else if (destination == null) {
            message = "Export completed, but saving was cancelled. Tap Create Short to export again."
            pendingOutput = null
            file?.delete()
        }
    }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedVideo = uri
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            videoDurationMs = runCatching {
                MediaMetadataRetriever().let { retriever ->
                    try {
                        retriever.setDataSource(context, uri)
                        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                    } finally {
                        retriever.release()
                    }
                }
            }.getOrDefault(0L)
            val durationSeconds = (videoDurationMs / 1000L).toInt()
            startSeconds = "0"
            endSeconds = minOf(60, durationSeconds.coerceAtLeast(1)).toString()
            message = if (videoDurationMs > 0) {
                "Video ready • ${durationSeconds}s long. Choose the part you want to export."
            } else {
                "Video selected. Duration could not be read; try another MP4 video."
            }
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Bg,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Accent, shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.AutoAwesome, null, Modifier.padding(12.dp), tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("ViralClip AI", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Create vertical Shorts on your phone", color = Muted, fontSize = 12.sp)
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("1. Add your video", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("YouTube link (open only)") },
                            placeholder = { Text("https://youtube.com/watch?v=...") },
                            singleLine = true
                        )
                        OutlinedButton(
                            onClick = {
                                val valid = url.contains("youtube.com/") || url.contains("youtu.be/")
                                if (valid) {
                                    runCatching {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    }.onFailure {
                                        message = "Could not open that link."
                                    }
                                    message = "Opened YouTube link. To edit, select a video file you own or have permission to use."
                                } else {
                                    message = "Enter a valid YouTube or youtu.be link."
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Open YouTube link") }
                        Button(
                            onClick = { picker.launch(arrayOf("video/mp4", "video/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VideoLibrary, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose video from phone")
                        }
                        selectedVideo?.let {
                            Text("Video selected", color = Color(0xFF86EFAC), fontWeight = FontWeight.SemiBold)
                            Text("Duration: ${videoDurationMs / 1000}s", color = Muted, fontSize = 12.sp)
                        }
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("2. Choose your clip", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Start time (seconds)", color = Muted)
                        OutlinedTextField(
                            value = startSeconds,
                            onValueChange = { startSeconds = it.filter(Char::isDigit).take(6) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Text("End time (seconds)", color = Muted)
                        OutlinedTextField(
                            value = endSeconds,
                            onValueChange = { endSeconds = it.filter(Char::isDigit).take(6) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Text("Export target: 9:16 vertical • MP4 • no watermark", color = Color(0xFF86EFAC), fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = {
                        val uri = selectedVideo
                        val start = startSeconds.toLongOrNull()
                        val end = endSeconds.toLongOrNull()
                        val durationSeconds = videoDurationMs / 1000L
                        if (uri == null) {
                            message = "Please choose a video first."
                        } else if (start == null || end == null || start < 0 || end <= start) {
                            message = "Enter valid start and end times. End must be greater than start."
                        } else if (videoDurationMs <= 0L || end > durationSeconds + 1L) {
                            message = "End time exceeds the video duration. Choose a shorter range."
                        } else if (end - start > 180L) {
                            message = "For phone performance, export clips up to 180 seconds at a time."
                        } else if (!isExporting) {
                            isExporting = true
                            message = "Preparing your vertical MP4… Keep the app open until export finishes."
                            val output = File(context.cacheDir, "viralclip_${System.currentTimeMillis()}.mp4")
                            val clipping = MediaItem.ClippingConfiguration.Builder()
                                .setStartPositionMs(start * 1000L)
                                .setEndPositionMs(end * 1000L)
                                .build()
                            val mediaItem = MediaItem.Builder()
                                .setUri(uri)
                                .setClippingConfiguration(clipping)
                                .build()
                            val verticalEffect = Presentation.createForWidthAndHeight(
                                1080, 1920, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP
                            )
                            val edited = EditedMediaItem.Builder(mediaItem)
                                .setEffects(Effects(emptyList(), listOf(verticalEffect)))
                                .build()
                            val transformer = Transformer.Builder(context)
                                .addListener(object : Transformer.Listener {
                                    override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                                        isExporting = false
                                        pendingOutput = output
                                        message = "Video export complete. Choose where to save your MP4."
                                        saveLauncher.launch("ViralClipAI_Short.mp4")
                                    }

                                    override fun onError(
                                        composition: Composition,
                                        exportResult: ExportResult,
                                        exportException: ExportException
                                    ) {
                                        isExporting = false
                                        output.delete()
                                        message = "Export failed: ${exportException.message ?: "unsupported video format or device encoder error"}"
                                    }
                                })
                                .build()
                            runCatching {
                                transformer.start(edited, output.absolutePath)
                            }.onFailure {
                                isExporting = false
                                output.delete()
                                message = "Could not start export: ${it.message ?: "unknown error"}"
                            }
                        }
                    },
                    enabled = !isExporting,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Exporting…")
                    } else {
                        Icon(Icons.Default.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Create vertical MP4", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(color = Color(0xFF201A35), shape = RoundedCornerShape(14.dp)) {
                    Text(message, Modifier.padding(14.dp), color = Color(0xFFE9D5FF), fontSize = 13.sp)
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Coming next", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("• Automatic highlight detection", color = Muted, fontSize = 13.sp)
                        Text("• Real Hindi + English speech captions", color = Muted, fontSize = 13.sp)
                        Text("• Caption styling and subject tracking", color = Muted, fontSize = 13.sp)
                        Text("This version performs local video trimming and 9:16 export. It does not yet create AI-selected highlights or burn captions.", color = Muted, fontSize = 11.sp)
                    }
                }
                Text(
                    "Free local export • No watermark added by this app. Use videos you own or are authorized to edit. YouTube links open in YouTube; the app does not bypass download restrictions.",
                    color = Muted, fontSize = 11.sp
                )
            }
        }
    }
}
