package com.example.note.presentation.ui.noteDetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.note.model.Note
import com.example.note.repository.NoteRepository
import com.example.note.util.NOTE_DETAIL_STATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(NoteDetailUiState())
        private set

    init {
        state.get<NoteDetailUiState>(NOTE_DETAIL_STATE)?.let { state ->
            uiState = state
        }
    }

    fun getNote(id: String) {
        viewModelScope.launch {
            uiState = try {
                val note = noteRepository.getNote(id = id)
                uiState.copy(note = note, title = note.title, body = note.body)
            } catch (e: Exception) {
                uiState.copy(errorMessage = e.message)
            }
            state.set<NoteDetailUiState>(NOTE_DETAIL_STATE, uiState)
        }
    }

    fun updateNote() {
        viewModelScope.launch {
            try {
                noteRepository.updateNote(
                    note = uiState.note!!.copy(
                        title = uiState.title,
                        body = uiState.body
                    )
                )
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNote(note)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message)
            }
        }
    }

    fun updateNoteTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateNoteBody(body: String) {
        uiState = uiState.copy(body = body)
    }
}