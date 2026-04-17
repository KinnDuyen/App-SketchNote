package com.example.sketchnote.ui.home

import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sketchnote.R
import com.example.sketchnote.data.local.entity.NoteEntity
import com.example.sketchnote.util.VoiceState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// ── QUẢN LÝ MÀU SẮC ──────────────────────────────────────────────────────────

fun colorTagToColor(tag: String): Color = when (tag) {
    "RED"    -> Color(0xFFFFCDD2)
    "ORANGE" -> Color(0xFFFFE0B2)
    "YELLOW" -> Color(0xFFFFF9C4)
    "GREEN"  -> Color(0xFFC8E6C9)
    "BLUE"   -> Color(0xFFBBDEFB)
    "PURPLE" -> Color(0xFFE1BEE7)
    else     -> Color(0xFFF5F5F5)
}

fun colorTagToDark(tag: String): Color = when (tag) {
    "RED"    -> Color(0xFFE57373)
    "ORANGE" -> Color(0xFFFFB74D)
    "YELLOW" -> Color(0xFFFFF176)
    "GREEN"  -> Color(0xFF81C784)
    "BLUE"   -> Color(0xFF64B5F6)
    "PURPLE" -> Color(0xFFBA68C8)
    else     -> Color(0xFFBDBDBD)
}

val figmaFilterEntries = listOf(
    ColorFilter.RED    to Color(0xFFDC9B9B),
    ColorFilter.ORANGE to Color(0xFFFF7444),
    ColorFilter.PURPLE to Color(0xFFB7BDF7),
    ColorFilter.GREEN  to Color(0xFFDAF9DE),
    ColorFilter.BLUE   to Color(0xFFCFECF3),
)

val yellowTheme = Color(0xFFF9D03E)

// ── HOMESCREEN ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onCreateNote: () -> Unit,
    onTrashClick: () -> Unit,
    onBackupClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val colorFilter by viewModel.colorFilter.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()

    val micPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    LaunchedEffect(Unit) { viewModel.initVoice(context) }

    val micScale by rememberInfiniteTransition(label = "mic").animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    Scaffold(
        containerColor = Color(0xFFFBFCF7),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                containerColor = yellowTheme,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Add, "Tạo", modifier = Modifier.size(36.dp), tint = Color.Black)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFCF7))
                .padding(innerPadding)
        ) {
            // ── HEADER BOX (Logo + Mèo + Search) ──────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // 1. Logo (Kích thước chuẩn)
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_sketchnote),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(85.dp)
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 8.dp),
                    contentScale = ContentScale.Fit
                )

                // 2. Nút Action (Backup & Trash)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HeaderActionIcon(icon = Icons.Default.Backup, onClick = onBackupClick)
                    HeaderActionIcon(icon = Icons.Default.Delete, onClick = onTrashClick)
                }

                // 3. Ảnh Mèo
                Image(
                    painter = painterResource(id = R.drawable.img_cats_party),
                    contentDescription = "Mèo banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.TopCenter)
                        .padding(
                            top = 65.dp,
                            end = 52.dp
                        )
                        .scale(1.2f),
                    contentScale = ContentScale.Fit
                )

                // 4. Search bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 45.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        shadowElevation = 8.dp,
                        color = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.Search, null, modifier = Modifier.size(22.dp), tint = Color.Gray)
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = viewModel::onSearchQueryChange,
                                singleLine = true,
                                modifier = Modifier.weight(1f).padding(start = 8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) Text("Tìm kiếm ghi chú...", color = Color.LightGray, fontSize = 15.sp)
                                    innerTextField()
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = CircleShape,
                        shadowElevation = 8.dp,
                        color = if (voiceState == VoiceState.LISTENING) yellowTheme else Color.White
                    ) {
                        IconButton(onClick = {
                            if (voiceState == VoiceState.LISTENING) viewModel.stopVoiceSearch()
                            else if (micPermission.status.isGranted) viewModel.startVoiceSearch()
                            else micPermission.launchPermissionRequest()
                        }) {
                            Icon(
                                imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mic",
                                modifier = Modifier.scale(if (voiceState == VoiceState.LISTENING) micScale else 1f),
                                tint = Color.Black
                            )
                        }
                    }
                }

                // 5. Filter pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    figmaFilterEntries.forEach { (filter, color) ->
                        val isSelected = colorFilter == filter
                        Box(
                            modifier = Modifier
                                .size(width = 55.dp, height = 28.dp)
                                .background(color, RoundedCornerShape(20.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) Color.Black.copy(0.2f) else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.onColorFilterChange(filter) }
                        )
                    }
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        color = if (colorFilter == ColorFilter.ALL) yellowTheme else Color.White
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { viewModel.onColorFilterChange(ColorFilter.ALL) }
                        ) {
                            Text(
                                text = "✓",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── LƯỚI GHI CHÚ ────────────────────────────────────────────
            if (notes.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có ghi chú nào.\nNhấn + để tạo mới!", textAlign = TextAlign.Center, color = Color.Gray)
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note.id) },
                            onDelete = { viewModel.moveToTrash(note.id) },
                            onTogglePin = { viewModel.togglePin(note) }
                        )
                    }
                }
            }
        }
    }
}

// ── COMPOSABLES PHỤ TRỢ ────────────────────────────────────────

@Composable
fun HeaderActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 3.dp,
        color = Color.White,
        modifier = Modifier.size(36.dp).clickable { onClick() }
    ) {
        Icon(icon, null, modifier = Modifier.padding(8.dp), tint = Color.Black)
    }
}

@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit, onDelete: () -> Unit, onTogglePin: () -> Unit) {
    val borderColor = colorTagToDark(note.colorTag)
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = Color.White,
        border = BorderStroke(3.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(note.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Icon(
                    if (note.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                    null,
                    modifier = Modifier.size(18.dp).clickable { onTogglePin() },
                    tint = if (note.isPinned) yellowTheme else Color.LightGray
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(note.content, fontSize = 13.sp, maxLines = 6, overflow = TextOverflow.Ellipsis, color = Color.Gray)
            Row(Modifier.padding(top = 10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (note.type == "CHECKLIST") "☑ Checklist" else "📝 Text", fontSize = 10.sp, color = Color.LightGray)
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = Color.LightGray)
                }
            }
        }
    }
}
