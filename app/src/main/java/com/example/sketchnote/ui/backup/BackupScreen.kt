package com.example.sketchnote.ui.backup

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Yellow      = Color(0xFFFCC701)
private val YellowLight = Color(0xFFFFF8E7)
private val YellowText  = Color(0xFF7A5F00)
private val BgPage      = Color(0xFFFBFCF7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    onBack: () -> Unit,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showImportDialog by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.importBackup(context, it) {
                showImportDialog = true
            }
        }
    }

    Scaffold(
        containerColor = BgPage,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Yellow, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, fontStyle = FontStyle.Italic)) { append("Sketch") }
                            withStyle(SpanStyle(color = Yellow, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)) { append("Note,") }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF333333))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgPage)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            // ── Icon trung tâm ─────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(YellowLight)
                    .border(2.dp, Yellow, CircleShape)
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Yellow,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Sao lưu dữ liệu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Text(
                "Giữ an toàn ghi chú của bạn",
                fontSize = 13.sp,
                color = Color(0xFF888888)
            )

            Spacer(Modifier.height(20.dp))

            // ── Info card ──────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = YellowLight,
                border = androidx.compose.foundation.BorderStroke(1.dp, Yellow)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Yellow)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        "Sao lưu sẽ lưu toàn bộ ghi chú ra file JSON trong thư mục Downloads để khôi phục khi cần.",
                        fontSize = 13.sp,
                        color = YellowText,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Nút Xuất Backup ────────────────────────────────────────────
            Button(
                onClick = { viewModel.exportBackup(context) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Xuất Backup (JSON)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            Spacer(Modifier.height(12.dp))

            // ── Nút Nhập Backup ────────────────────────────────────────────
            OutlinedButton(
                onClick = { importLauncher.launch("application/json") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Yellow),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Yellow, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Nhập Backup từ file", fontWeight = FontWeight.Bold, color = Yellow, fontSize = 15.sp)
            }

            Spacer(Modifier.weight(1f))

            // ── Bottom hint ────────────────────────────────────────────────
            Text(
                "Dữ liệu được lưu cục bộ trên máy",
                fontSize = 12.sp,
                color = Color(0xFFBBBBBB)
            )

            Spacer(Modifier.height(20.dp))
        }
    }

    // ── Dialog import thành công ───────────────────────────────────────────
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text("Khôi phục thành công!", fontWeight = FontWeight.Bold, color = Color(0xFF222222))
            },
            text = {
                Text("Dữ liệu đã được nhập vào app.", color = Color(0xFF555555))
            },
            confirmButton = {
                Button(
                    onClick = { showImportDialog = false; onBack() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
