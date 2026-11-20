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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.AutoAwesome
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
    var language by remember { mutableStateOf("English") }
    var clipLength by remember { mutableStateOf("30–60 sec") }
    var message by remember { mutableStateOf("Choose a video file to prepare a clip project.") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            selectedVideo = uri
            context.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            message = "Video selected. The editing engine is not connected in this prototype."
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
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Accent, shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.AutoAwesome, null,
                            modifier = Modifier.padding(12.dp), tint = Color.White)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("ViralClip AI", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Turn long videos into short-form ideas", color = Muted, fontSize = 12.sp)
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("1. Add your video", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("YouTube URL (for permitted access)") },
                            placeholder = { Text("https://www.youtube.com/watch?v=...") },
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                message = if (url.contains("youtube.com") || url.contains("youtu.be")) {
                                    "Link entered. Direct downloading/processing is not enabled; use a video you own or have permission to edit."
                                } else "Please enter a valid YouTube link."
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentPaste, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Check link")
                        }
                        OutlinedButton(
                            onClick = { picker.launch(arrayOf("video/*")) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.VideoLibrary, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Choose video from phone")
                        }
                        selectedVideo?.let { Text("Selected: ${it.lastPathSegment ?: "Video"}", color = Color(0xFF86EFAC)) }
                    }
                }

                Surface(color = Panel, shape = RoundedCornerShape(20.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("2. Clip settings", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Clip duration", color = Muted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("15–30 sec", "30–60 sec", "60–90 sec").forEach {
                                FilterChip(selected = clipLength == it, onClick = { clipLength = it }, label = { Text(it) })
                            }
                        }
                        Text("Caption language", color = Muted)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("English", "Hindi", "Odia").forEach {
                                FilterChip(selected = language == it, onClick = { language = it }, label = { Text(it) })
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Output format", color = Muted)
                            Spacer(Modifier.weight(1f))
                            Text("9:16 Vertical", color = Color(0xFF86EFAC), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Button(
                    onClick = {
                        message = "Prototype only: automatic highlight detection, speech transcription, caption burn-in and MP4 rendering still need to be implemented."
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create Shorts", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Surface(color = Color(0xFF201A35), shape = RoundedCornerShape(14.dp)) {
                    Text(message, modifier = Modifier.padding(14.dp), color = Color(0xFFE9D5FF), fontSize = 13.sp)
                }

                Text("Planned pipeline", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                listOf(
                    "Detect engaging moments automatically",
                    "Transcribe speech and generate synced captions",
                    "Reframe to 9:16 with subject tracking",
                    "Render and export MP4 to your phone"
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
                    "Version 0.1 prototype • No subscription or daily quota is enforced by this demo. Processing features are not yet active.",
                    color = Muted, fontSize = 11.sp
                )
            }
        }
    }
}
