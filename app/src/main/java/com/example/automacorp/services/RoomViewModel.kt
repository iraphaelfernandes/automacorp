// RoomViewModel.kt
package com.example.automacorp.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automacorp.models.RoomCommandDto
import com.example.automacorp.models.RoomDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

data class RoomListState(
    val rooms: List<RoomDto> = emptyList(),
    val error: String? = null,
    val selectedRoom: RoomDto? = null,
)

class RoomViewModel : ViewModel() {
    private val _roomsState = MutableStateFlow(RoomListState())
    val roomsState: StateFlow<RoomListState> = _roomsState

    fun findAll() {
        executeApiCall {
            val response = ApiServices.roomsApiService.findAll().execute()
            val rooms = response.body() ?: emptyList()
            _roomsState.value = RoomListState(rooms = rooms)
        }
    }

    fun selectRoom(room: RoomDto) {
        _roomsState.value = _roomsState.value.copy(
            selectedRoom = room
        )
    }

    fun clearSelection() {
        _roomsState.value = _roomsState.value.copy(
            selectedRoom = null
        )
    }

    fun findRoom(id: Long) {
        executeApiCall {
            val response = ApiServices.roomsApiService.findById(id).execute()
            val room = response.body()
            _roomsState.value = _roomsState.value.copy(selectedRoom = room)
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = createRoomCommand(roomDto)
        executeApiCall {
            val response = ApiServices.roomsApiService.updateRoom(id, command).execute()
            val updatedRoom = response.body()
            _roomsState.value = _roomsState.value.copy(selectedRoom = updatedRoom)
        }
    }

    private fun createRoomCommand(roomDto: RoomDto) = RoomCommandDto(
        name = roomDto.name,
        targetTemperature = roomDto.targetTemperature?.let { roundToOneDecimal(it) },
        currentTemperature = roomDto.currentTemperature,
    )

    private fun roundToOneDecimal(value: Double): Double {
        return round(value * 10) / 10.0
    }

    private fun executeApiCall(apiCall: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                apiCall()
            } catch (e: Exception) {
                handleApiError(e)
            }
        }
    }

    private fun handleApiError(error: Exception) {
        error.printStackTrace()
        _roomsState.value = _roomsState.value.copy(
            error = error.stackTraceToString()
        )
    }
}