package com.example.note.business.interactors.common

import com.example.note.business.data.cache.NoteCacheRepository
import com.example.note.business.data.network.NoteNetworkRepository
import com.example.note.business.data.util.CacheResponseHandler
import com.example.note.business.data.util.safeApiCall
import com.example.note.business.data.util.safeCacheCall
import com.example.note.business.domain.model.Note
import com.example.note.business.domain.state.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNote<ViewState>(
    private val noteCacheRepository: NoteCacheRepository,
    private val noteNetworkRepository: NoteNetworkRepository
){

    fun deleteNote(
        note: Note,
        stateEvent: StateEvent
    ): Flow<DataState<ViewState>?> = flow {

        val cacheResult = safeCacheCall(IO){
            noteCacheRepository.deleteNote(note.id)
        }

        val response = object: CacheResponseHandler<ViewState, Int>(
            response = cacheResult,
            stateEvent = stateEvent
        ){
            override suspend fun handleSuccess(resultObj: Int): DataState<ViewState> {
                return if(resultObj > 0){
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_SUCCESS,
                            uiType = UiType.SnackBar, //send undo snackbar here
                            messageType = MessageType.Success
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
                else{
                    DataState.data(
                        response = Response(
                            message = DELETE_NOTE_FAILED,
                            uiType = UiType.SnackBar,
                            messageType = MessageType.Error
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }
        }.getResult()

        emit(response)

        // update network
        if(response?.stateMessage?.response?.message.equals(DELETE_NOTE_SUCCESS)){

            // delete from 'notes' node
            safeApiCall(IO){
                noteNetworkRepository.deleteNote(note.id)
            }

            // insert into 'deletes' node
            safeApiCall(IO){
                noteNetworkRepository.insertDeletedNote(note)
            }

        }
    }

    companion object{
        val DELETE_NOTE_SUCCESS = "Successfully deleted note."
        val DELETE_NOTE_PENDING = "Delete pending..."
        val DELETE_NOTE_FAILED = "Failed to delete note."
        val DELETE_ARE_YOU_SURE = "Are you sure you want to delete this?"
    }
}












