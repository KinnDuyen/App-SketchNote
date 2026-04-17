package com.example.sketchnote.ui.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.*

private val Yellow      = Color(0xFFFEC904)
private val YellowLight = Color(0xFFFDC904).copy(alpha = 0.7f)
private val BgColor     = Color(0xFFFBFCF7)

// Màu viền card theo colorTag — giống EditorScreen
private fun tagBorderColor(tag: String): Color = when (tag) {
    "GREEN"  -> Color(0xFFDAF9DE)
    "BLUE"   -> Color(0xFFCFECF3)
    "RED"    -> Color(0xFFFCEAEA)
    "ORANGE" -> Color(0xFFFFF0E0)
    "PURPLE" -> Color(0xFFEEECFF)
    else     -> Color(0xFFE0E0E0)
}

@Composable
fun TrashScreen(
    onBack: () -> Unit,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val notes by viewModel.deletedNotes.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header vàng bo góc dưới ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                    .background(Yellow)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Nút back — icon mũi tên xoay 90 độ
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Quay lại",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Tiêu đề giữa
                Text(
                    text = "Thùng rác",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Dọn sạch — góc phải
                if (notes.isNotEmpty()) {
                    TextButton(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(
                            "Dọn sạch",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            // ── Body ─────────────────────────────────────────────────────
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Thùng rác trống",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = 16.dp, bottom = 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // ── "Đã xóa gần đây" ─────────────────────────────────
                    item {
                        Text(
                            text = "Đã xóa gần đây",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Yellow,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // ── Card lưu ý viền vàng ──────────────────────────────
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .then(
                                    Modifier.padding(1.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                            ) {
                                // viền vàng
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.White,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Yellow),
                                    shadowElevation = 4.dp
                                ) {
                                    Text(
                                        text = "Lưu ý: Ghi chú sẽ tự động xóa sau 30 ngày",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = YellowLight,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    // ── Danh sách ghi chú ─────────────────────────────────
                    items(notes, key = { it.id }) { note ->
                        TrashNoteCard(
                            note = note,
                            onRestore = { viewModel.restore(note.id) },
                            onDelete = { viewModel.deletePermanently(note.id) }
                        )
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }
    }

    // ── Confirm dialog ─────────────────────────────────────────────────────
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Dọn sạch thùng rác?") },
            text = { Text("Tất cả ghi chú sẽ bị xóa vĩnh viễn, không thể khôi phục.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.emptyTrash()
                    showConfirmDialog = false
                }) { Text("Xóa hết", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun TrashNoteCard(
    note: NoteEntity,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val deleteAt  = note.updatedAt + 30L * 24 * 60 * 60 * 1000
    val daysLeft  = ((deleteAt - System.currentTimeMillis()) / (1000L * 60 * 60 * 24)).toInt()
    val dateStr   = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(deleteAt))
    val borderClr = tagBorderColor(note.colorTag)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(3.dp, borderClr),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {

            // Dòng ngày tự xóa — nhỏ, mờ, ở trên cùng
            Text(
                text = "Tự động xóa sau $daysLeft ngày ($dateStr)",
                fontSize = 11.sp,
                color = if (daysLeft <= 3) Color(0xFFC90505) else Color(0xFF888888),
                fontWeight = FontWeight.Normal
            )

            Spacer(Modifier.height(6.dp))

            // Nội dung ghi chú
            if (note.title.isNotBlank()) {
                Text(
                    text = note.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = note.content,
                    fontSize = 14.sp,
                    color = Color.Black.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // Nút hành động — căn phải
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onRestore,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Khôi phục",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }
                Spacer(Modifier.width(4.dp))
                TextButton(
                    onClick = onDelete,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Xóa vĩnh viễn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFC90505)
                    )
                }
            }
        }
    }
}
