package com.example.sketchnote.ui.home
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sketchnote.R
import com.example.sketchnote.ui.theme.*

val figmaFilterColors = listOf(
    Color(0xFFDC9B9B),
    Color(0xFFFF7444),
    Color(0xFFB7BDF7),
    Color(0xFFDAF9DE),
    Color(0xFFCFECF3),
)

@Composable
fun HomeScreen(
    onNoteClick: (Int) -> Unit,
    onCreateNote: () -> Unit,
    onTrashClick: () -> Unit,
    onBackupClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()
    val yellowTheme = Color(0xFFF9D03E)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNote,
                containerColor = yellowTheme,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Tạo ghi chú",
                    modifier = Modifier.size(36.dp),
                    tint = Color.Black
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            HomeBottomNavigation(
                yellowTheme = yellowTheme,
                onTrashClick = onTrashClick,
                onHomeClick = onHomeClick,
                onBackupClick = onBackupClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFCF7))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── 1+2+3+4. HEADER + MÈO + SEARCH + FILTER (overlap) ─────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                // Mèo
                Image(
                    painter = painterResource(id = R.drawable.img_cats_party),
                    contentDescription = "Mèo banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 17.dp, end = 60.dp),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.BottomCenter
                )

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo_sketchnote),
                    contentDescription = "Logo SketchNote",
                    modifier = Modifier
                        .height(130.dp)
                        .fillMaxWidth(0.72f)
                        .align(Alignment.TopStart)
                        .padding(start = 16.dp, top = 6.dp),
                    contentScale = ContentScale.FillHeight,
                    alignment = Alignment.CenterStart
                )

                // Search bar chồng lên phần dưới mèo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Icon(
                                Icons.Default.Search, null,
                                modifier = Modifier.size(22.dp),
                                tint = Color.Black.copy(alpha = 0.7f)
                            )
                            Text(
                                "Tìm kiếm ghi chú...",
                                modifier = Modifier.padding(start = 8.dp),
                                color = Color.Black.copy(alpha = 0.7f),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(width = 52.dp, height = 50.dp),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Mic,
                                contentDescription = "Mic",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }

                // Filter pills sát đáy Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp)
                        .align(Alignment.BottomCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    figmaFilterColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(width = 55.dp, height = 27.dp)
                                .background(color, RoundedCornerShape(20.dp))
                                .clickable { }
                        )
                    }
                    Surface(
                        modifier = Modifier.size(width = 28.dp, height = 27.dp),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── 5. LƯỚI GHI CHÚ ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NoteCard(
                    modifier = Modifier.fillMaxWidth().height(107.dp),
                    borderColor = Color(0xFFDC9B9B)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    NoteCard(
                        modifier = Modifier.weight(195f).height(260.dp),
                        borderColor = Color(0xFFB7BDF7)
                    )
                    NoteCard(
                        modifier = Modifier.weight(188f).height(260.dp),
                        borderColor = Color(0xFFCFECF3)
                    )
                }

                NoteCard(
                    modifier = Modifier.fillMaxWidth().height(141.dp).padding(top = 8.dp),
                    borderColor = Color(0xFFFF7444),
                    borderWidth = 3.dp
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    borderColor: Color,
    borderWidth: androidx.compose.ui.unit.Dp = 3.dp
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = Color.White,
        border = BorderStroke(borderWidth, borderColor)
    ) {}
}

// ── BOTTOM NAVIGATION ─────────────────────────────────────────────────────────
@Composable
fun HomeBottomNavigation(
    yellowTheme: Color,
    onTrashClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBackupClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 16.dp)
            .fillMaxWidth()
            .height(85.dp),
        shape = RoundedCornerShape(30.dp),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem(iconRes = R.drawable.ic_list, label = "Hồ sơ", iconColor = yellowTheme, onClick = onTrashClick)
            NavigationItem(iconRes = R.drawable.ic_home, label = "Trang chủ", iconColor = yellowTheme, onClick = onHomeClick)
            NavigationItem(iconRes = R.drawable.ic_chart, label = "Thống kê", iconColor = yellowTheme, onClick = onBackupClick)
        }
    }
}

@Composable
fun NavigationItem(
    iconRes: Int,
    label: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}