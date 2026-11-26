package com.viralclipai.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

@Composable
fun ViralClipApp() {
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }
    var message by remember {
        mutableStateOf("Add your own video or a video you have permission to edit.")
    }
    var clipLength by remember { mutableStateOf("30–60 sec") }

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
            message = "Video selected successfully. Automatic clipping and caption rendering are not active in this preview."
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
                        Text("Your Shorts studio", color = Muted, fontSize = 12.sp)
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("1. Add a video", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("YouTube link") },
                            placeholder = { Text("https://youtube.com/watch?v=...") },
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                val valid = url.contains("youtube.com/") || url.contains("youtu.be/")
                                message = if (valid) {
                                    "Link format looks valid. This preview does not download YouTube videos. Import an authorized video file below."
                                } else "Enter a valid YouTube or youtu.be link."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Check YouTube link") }

                        OutlinedButton(
                            onClick = { picker.launch(arrayOf("video/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VideoLibrary, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose video from phone")
                        }
                        selectedVideo?.let {
                            Text("Selected: ${it.lastPathSegment ?: "Video file"}", color = Color(0xFF86EFAC))
                        }
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("2. Short settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Clip duration", color = Muted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("15–30 sec", "30–60 sec", "60–90 sec").forEach {
                                FilterChip(
                                    selected = clipLength == it,
                                    onClick = { clipLength = it },
                                    label = { Text(it) }
                                )
                            }
                        }
                        Text("Caption languages", color = Muted)
                        Surface(color = Color(0xFF29213F), shape = RoundedCornerShape(10.dp)) {
                            Text("Hindi + English", Modifier.padding(12.dp), color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Output", color = Muted)
                            Spacer(Modifier.weight(1f))
                            Text("9:16 Vertical • MP4 target", color = Color(0xFF86EFAC), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = {
                        message = "Preview only: the video editing engine, automatic highlight detection, Hindi/English transcription, subtitle burn-in and MP4 export still need implementation."
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create Shorts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Surface(color = Color(0xFF201A35), shape = RoundedCornerShape(14.dp)) {
                    Text(message, Modifier.padding(14.dp), color = Color(0xFFE9D5FF), fontSize = 13.sp)
                }

                Text("Planned editing pipeline", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                listOf(
                    "Find engaging moments in your video",
                    "Generate synced Hindi and English captions",
                    "Reframe video to vertical 9:16",
                    "Export clean MP4 clips without a watermark"
                ).forEachIndexed { index, item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Accent.copy(alpha = .22f), shape = RoundedCornerShape(50)) {
                            Text("${index + 1}", Modifier.padding(horizontal = 11.dp, vertical = 6.dp), color = Color.White)
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(item, color = Muted, fontSize = 13.sp)
                    }
                }
                Text(
                    "Free preview • No subscription or daily quota in this app shell. Processing features are not yet active. Use only videos you own or are authorized to edit.",
                    color = Muted, fontSize = 11.sp
                )
            }
        }
    }
}
