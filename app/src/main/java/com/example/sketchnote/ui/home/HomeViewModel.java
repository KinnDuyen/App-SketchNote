package com.example.sketchnote.ui.home

import androidx.lifecycle.ViewModel
import com.example.sketchnote.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        // Mock dữ liệu giống như trong bản vẽ thiết kế của bạn
        _notes.value = listOf(
                Note(1, "", "", 0xFFDC9B9B), // Rectangle 538
                Note(2, "", "", 0xFFB7BDF7), // Rectangle 539
                Note(3, "", "", 0xFFCFECF3), // Rectangle 540
                Note(4, "", "", 0xFFFF7444)  // Rectangle 541
        )
    }
}package com.example.sketchnote.ui.home

import androidx.lifecycle.ViewModel
import com.example.sketchnote.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        // Mock dữ liệu giống như trong bản vẽ thiết kế của bạn
        _notes.value = listOf(
                Note(1, "", "", 0xFFDC9B9B), // Rectangle 538
                Note(2, "", "", 0xFFB7BDF7), // Rectangle 539
                Note(3, "", "", 0xFFCFECF3), // Rectangle 540
                Note(4, "", "", 0xFFFF7444)  // Rectangle 541
        )
    }
}