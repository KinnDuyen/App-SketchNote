package com.example.sketchnote.ui.editor

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sketchnote.R
import com.example.sketchnote.data.local.entity.NoteEntity
import com.example.sketchnote.utils.AutoTagUtils
import com.example.sketchnote.utils.ExportUtils
import com.example.sketchnote.utils.MathCalculator
import com.example.sketchnote.utils.OcrUtils
import com.example.sketchnote.utils.SpeechHelper
import java.io.File

// ── Màu helper ───────────────────────────────────────────────────────────────
fun tagToBoxColor(tag: String): Color = when (tag) {
    "RED"    -> Color(0xFFFCEAEA)
    "ORANGE" -> Color(0xFFFFF0E0)
    "PURPLE" -> Color(0xFFEEECFF)
    "GREEN"  -> Color(0xFFE8FAEC)
    "BLUE"   -> Color(0xFFE4F4FB)
    else     -> Color.White
}

fun tagToPillColor(tag: String): Color = when (tag) {
    "RED"    -> Color(0xFFDC9B9B)
    "ORANGE" -> Color(0xFFFF7444)
    "PURPLE" -> Color(0xFFB7BDF7)
    "GREEN"  -> Color(0xFFDAF9DE)
    "BLUE"   -> Color(0xFFCFECF3)
    else     -> Color.White
}

fun tagToBorderColor(tag: String): Color = when (tag) {
    "RED"    -> Color(0xFFB06060)
    "ORANGE" -> Color(0xFFCC4420)
    "PURPLE" -> Color(0xFF7A80D0)
    "GREEN"  -> Color(0xFF7AC880)
    "BLUE"   -> Color(0xFF7ABCCC)
    else     -> Color(0xFF999999)
}

@Composable
fun SketchNoteLogo(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = Color(0xFFFCC701), fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp, fontStyle = FontStyle.Italic
                )) { append("Sketch") }
                withStyle(SpanStyle(
                    color = Color(0xFFFCC701), fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp, fontStyle = FontStyle.Normal
                )) { append("Note,") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Int,
    onBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    LaunchedEffect(noteId) { viewModel.loadNote(noteId) }

    val context       = LocalContext.current
    val title         by viewModel.title.collectAsStateWithLifecycle()
    val colorTag      by viewModel.colorTag.collectAsStateWithLifecycle()
    val imagePaths    by viewModel.imagePaths.collectAsStateWithLifecycle()
    val reminderTime  by viewModel.reminderTime.collectAsStateWithLifecycle()
    val type          by viewModel.type.collectAsStateWithLifecycle()
    val contentBlocks by viewModel.contentBlocks.collectAsStateWithLifecycle()

    var showHighlightPicker     by remember { mutableStateOf(false) }
    var currentHighlightBlockId by remember { mutableStateOf<String?>(null) }
    var currentSelectionRange   by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showSketchDialog        by remember { mutableStateOf(false) }
    var editingSketchBlockId    by remember { mutableStateOf<String?>(null) }
    var activeTool              by remember { mutableStateOf<String?>(null) }
    var cameraUri               by remember { mutableStateOf<Uri?>(null) }
    var showExportMenu          by remember { mutableStateOf(false) }
    val speechHelper            = remember { SpeechHelper(context) }
    var isListening             by remember { mutableStateOf(false) }

    var suggestedTag by remember { mutableStateOf<String?>(null) }
    val allText = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
        .joinToString(" ") { it.text }
    LaunchedEffect(title, allText) {
        kotlinx.coroutines.delay(800)
        val tag = AutoTagUtils.suggestTag(title, allText)
        if (tag != null && tag != colorTag) suggestedTag = tag
    }
}