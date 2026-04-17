LazyColumn(
modifier = Modifier.fillMaxWidth().weight(1f),
verticalArrangement = Arrangement.spacedBy(0.dp)
) {
    item { ColorTagPicker(selected = colorTag, onSelect = viewModel::onColorTagChange) }
    item {
        ReminderSection(
            reminderTime = reminderTime,
            onSetReminder = { viewModel.onReminderChange(it) },
            onClearReminder = { viewModel.onReminderChange(0L) }
        )
    }

    if (suggestedTag != null && suggestedTag != colorTag) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Gợi ý nhãn: $suggestedTag", fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    TextButton(onClick = {
                        viewModel.onColorTagChange(suggestedTag!!); suggestedTag = null
                    }) { Text("Áp dụng") }
                    TextButton(onClick = { suggestedTag = null }) { Text("Bỏ qua") }
                }
            }
        }
    }

    item {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    }

    item {
        TextField(
            value = title, onValueChange = viewModel::onTitleChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            placeholder = {
                Text(
                    "Tiêu đề", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }

    if (imagePaths.isNotEmpty()) {
        item {
            Text(
                "Ảnh đính kèm", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 2.dp)
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                itemsIndexed(imagePaths) { index, path ->
                    Box(modifier = Modifier.size(100.dp)) {
                        AsyncImage(
                            model = if (path.startsWith("content://")) Uri.parse(path)
                            else File(path),
                            contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                        )
                        IconButton(
                            onClick = { viewModel.removeImagePath(index) },
                            modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                Icons.Default.Close, null, tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
        item {
            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )
        }
    }
}
}
}
}

// ── ActionToolButton — vòng tròn vàng khi active ─────────────────────────────
@Composable
private fun ActionToolButton(
    drawableRes: Int,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .then(if (isActive)
                    Modifier.background(Color(0xFFFCC701).copy(alpha = 0.15f), CircleShape)
                else Modifier)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        ) {
            Icon(painter = painterResource(drawableRes), contentDescription = label,
                tint = Color.Unspecified, modifier = Modifier.size(22.dp))
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(5.dp).clip(CircleShape)
                .background(if (isActive) Color(0xFFFCC701) else Color.Transparent)
        )
    }
}

// ── ColorTagPicker ────────────────────────────────────────────────────────────
@Composable
fun ColorTagPicker(selected: String, onSelect: (String) -> Unit) {
    val tags = listOf("NONE", "RED", "ORANGE", "PURPLE", "GREEN", "BLUE")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        itemsIndexed(tags) { _, tag ->
            val sel = selected == tag
            Box(
                modifier = Modifier
                    .height(26.dp).width(58.dp)
                    .shadow(elevation = if (sel) 0.dp else 3.dp, shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(tagToPillColor(tag))
                    .border(
                        width = if (sel) 2.dp else 0.dp,
                        color = if (sel) tagToBorderColor(tag) else Color.Transparent,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(tag) }
            )
        }
    }
}

// ── ReminderSection ───────────────────────────────────────────────────────────
@Composable
fun ReminderSection(
    reminderTime: Long,
    onSetReminder: (Long) -> Unit,
    onClearReminder: () -> Unit
) {
    val context = LocalContext.current
    val hasReminder = reminderTime > System.currentTimeMillis()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(48.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp), color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(painter = painterResource(R.drawable.ring), contentDescription = null,
                    tint = Color.Unspecified, modifier = Modifier.size(24.dp))
            }
        }
        Surface(
            modifier = Modifier.height(48.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp), color = Color.White
        ) {
            if (hasReminder) {
                Row(modifier = Modifier.padding(horizontal = 14.dp).fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(reminderTime)),
                        fontSize = 13.sp, color = Color(0xFFE8B800),
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = onClearReminder, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    }
                }
            } else {
                TextButton(
                    onClick = {
                        val cal = java.util.Calendar.getInstance()
                        android.app.DatePickerDialog(context, { _, y, m, d ->
                            android.app.TimePickerDialog(context, { _, h, min ->
                                cal.set(y, m, d, h, min, 0); onSetReminder(cal.timeInMillis)
                            }, cal.get(java.util.Calendar.HOUR_OF_DAY),
                                cal.get(java.util.Calendar.MINUTE), true).show()
                        }, cal.get(java.util.Calendar.YEAR),
                            cal.get(java.util.Calendar.MONTH),
                            cal.get(java.util.Calendar.DAY_OF_MONTH)).show()
                    },
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Nhắc nhở", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333))
                    }
                }
            }
        }
    }
}