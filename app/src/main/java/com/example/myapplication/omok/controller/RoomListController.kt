package com.example.myapplication.omok.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.omok.data.GameRoomEntity
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.data.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 방 만들기 실패 사유. 어떤 문구로 보여줄지는 View가 결정
enum class RoomMessage {
    EMPTY_TITLE,
    PLAYER_NOT_SELECTED,
    SAME_PLAYER
}

// View가 방 목록 화면을 그리는 데 필요한 상태 스냅샷
// (사용자 목록은 흑돌·백돌을 맡을 사람을 고르는 데 필요하다)
data class RoomListUiState(
    val rooms: List<GameRoomEntity>,
    val users: List<UserEntity>,
    val message: RoomMessage?
)

// 방 목록 조회·방 만들기를 담당하는 Controller
class RoomListController(
    private val repository: OmokRepository,
    private val scope: CoroutineScope,
) {

    var uiState by mutableStateOf(
        RoomListUiState(rooms = emptyList(), users = emptyList(), message = null)
    )
        private set

    // 방 목록과 (선택지로 쓸) 사용자 목록을 함께 읽는다
    fun loadRooms() {
        scope.launch {
            uiState = uiState.copy(
                rooms = repository.getRooms(),
                users = repository.getUsers(),
            )
        }
    }

    // 방 만들기: 제목·대국자 선택을 검증하고 저장 후 목록을 새로 읽는다
    fun onCreateRoom(title: String, blackUserId: Long?, whiteUserId: Long?) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            uiState = uiState.copy(message = RoomMessage.EMPTY_TITLE)
            return
        }
        if (blackUserId == null || whiteUserId == null) {
            uiState = uiState.copy(message = RoomMessage.PLAYER_NOT_SELECTED)
            return
        }
        if (blackUserId == whiteUserId) {
            uiState = uiState.copy(message = RoomMessage.SAME_PLAYER)
            return
        }
        scope.launch {
            repository.createRoom(trimmedTitle, blackUserId, whiteUserId)
            uiState = uiState.copy(rooms = repository.getRooms(), message = null)
        }
    }
}
