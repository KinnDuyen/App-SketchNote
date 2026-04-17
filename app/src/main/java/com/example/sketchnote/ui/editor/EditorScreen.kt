@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Int,
    onBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    LaunchedEffect(noteId) { viewModel.loadNote(noteId) }

    val context = LocalContext.current
    val title by viewModel.title.collectAsStateWithLifecycle()
    val colorTag by viewModel.colorTag.collectAsStateWithLifecycle()
    val imagePaths by viewModel.imagePaths.collectAsStateWithLifecycle()
    val reminderTime by viewModel.reminderTime.collectAsStateWithLifecycle()
    val type by viewModel.type.collectAsStateWithLifecycle()
    val contentBlocks by viewModel.contentBlocks.collectAsStateWithLifecycle()

    var showHighlightPicker by remember { mutableStateOf(false) }
    var currentHighlightBlockId by remember { mutableStateOf<String?>(null) }
    var currentSelectionRange by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var showSketchDialog by remember { mutableStateOf(false) }
    var editingSketchBlockId by remember { mutableStateOf<String?>(null) }
    var activeTool by remember { mutableStateOf<String?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var showExportMenu by remember { mutableStateOf(false) }
    val speechHelper = remember { SpeechHelper(context) }
    var isListening by remember { mutableStateOf(false) }

    var suggestedTag by remember { mutableStateOf<String?>(null) }
    val allText = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
        .joinToString(" ") { it.text }
    LaunchedEffect(title, allText) {
        kotlinx.coroutines.delay(800)
        val tag = AutoTagUtils.suggestTag(title, allText)
        if (tag != null && tag != colorTag) suggestedTag = tag
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) cameraUri?.let { viewModel.addImagePath(it.toString()) }
        activeTool = null
    }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    ?: context.filesDir
                dir.mkdirs()
                val file = File(dir, "photo_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context, "${context.packageName}.fileprovider", file
                )
                cameraUri = uri
                cameraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Không thể mở camera", Toast.LENGTH_SHORT).show()
                activeTool = null
            }
        } else {
            Toast.makeText(context, "Cần quyền camera", Toast.LENGTH_SHORT).show()
            activeTool = null
        }
    }

    val micPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            isListening = true; activeTool = "mic"
            speechHelper.startListening(
                onResult = { text ->
                    val id = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
                        .lastOrNull()?.id
                    if (id != null) {
                        val cur = (contentBlocks.find { it.id == id }
                                as? ContentBlock.TextBlock)?.text ?: ""
                        viewModel.updateTextBlock(id, "$cur $text")
                    }
                    isListening = false; activeTool = null
                },
                onError = { isListening = false; activeTool = null }
            )
        }
    }

    val ocrLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            OcrUtils.recognizeText(
                context = context, imageUri = it,
                onSuccess = { text ->
                    val id = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
                        .lastOrNull()?.id
                    if (id != null) {
                        val cur = (contentBlocks.find { it.id == id }
                                as? ContentBlock.TextBlock)?.text ?: ""
                        viewModel.updateTextBlock(id, "$cur\n$text")
                    }
                },
                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
            )
        }
        activeTool = null
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> uris.forEach { viewModel.addImagePath(it.toString()) }; activeTool = null }

    Scaffold(containerColor = Color(0xFFFBFCF7)) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 18.dp).padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SketchNoteLogo(modifier = Modifier.height(46.dp))
                IconButton(
                    onClick = { viewModel.shareNote(context) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.share), contentDescription = "Chia sẻ",
                        tint = Color.Unspecified, modifier = Modifier.size(24.dp)
                    )
                }
            }

            // ── Back + Action bar ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { viewModel.saveNote(context, onBack) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = "Quay lại",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    modifier = Modifier.weight(1f)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp)),
                    color = Color.White, shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ActionToolButton(R.drawable.mic, "Ghi âm", activeTool == "mic") {
                            if (isListening) {
                                speechHelper.stopListening(); isListening = false; activeTool = null
                            } else {
                                activeTool =
                                    "mic"; micPermLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                        ActionToolButton(R.drawable.pen, "Vẽ", activeTool == "sketch") {
                            activeTool = null; editingSketchBlockId = null; showSketchDialog = true
                        }
                        Box(contentAlignment = Alignment.Center) {
                            ActionToolButton(
                                R.drawable.send, "Xuất",
                                activeTool == "export" || showExportMenu
                            ) {
                                activeTool = "export"; showExportMenu = true
                            }
                            DropdownMenu(
                                expanded = showExportMenu,
                                onDismissRequest = { showExportMenu = false; activeTool = null }) {
                                DropdownMenuItem(
                                    text = { Text("Xuất ảnh JPG") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Image,
                                            null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showExportMenu = false; activeTool = null
                                        viewModel.saveNote(context) {
                                            val note = NoteEntity(
                                                id = noteId.coerceAtLeast(0), title = title,
                                                content = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
                                                    .joinToString("\n") { it.text },
                                                type = type, colorTag = colorTag,
                                                imagePaths = imagePaths.joinToString("||"),
                                                reminderTime = reminderTime
                                            )
                                            ExportUtils.exportAsImage(context, note)
                                                ?.let {
                                                    ExportUtils.shareFile(
                                                        context,
                                                        it,
                                                        "image/jpeg"
                                                    )
                                                }
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Xuất PDF") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.PictureAsPdf,
                                            null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    onClick = {
                                        showExportMenu = false; activeTool = null
                                        viewModel.saveNote(context) {
                                            val note = NoteEntity(
                                                id = noteId.coerceAtLeast(0), title = title,
                                                content = contentBlocks.filterIsInstance<ContentBlock.TextBlock>()
                                                    .joinToString("\n") { it.text },
                                                type = type, colorTag = colorTag,
                                                imagePaths = imagePaths.joinToString("||"),
                                                reminderTime = reminderTime
                                            )
                                            ExportUtils.exportAsPdf(context, note)
                                                ?.let {
                                                    ExportUtils.shareFile(
                                                        context,
                                                        it,
                                                        "application/pdf"
                                                    )
                                                }
                                        }
                                    }
                                )
                            }
                        }
                        ActionToolButton(R.drawable.scan, "Quét chữ", activeTool == "ocr") {
                            activeTool = "ocr"; ocrLauncher.launch("image/*")
                        }
                        ActionToolButton(R.drawable.cam, "Chụp ảnh", activeTool == "cam") {
                            activeTool =
                                "cam"; cameraPermLauncher.launch(Manifest.permission.CAMERA)
                        }
                        ActionToolButton(R.drawable.image, "Thêm ảnh", activeTool == "gallery") {
                            activeTool = "gallery"; galleryLauncher.launch("image/*")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}