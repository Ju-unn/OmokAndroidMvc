package com.example.myapplication.omok.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.data.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// 사용자 추가 실패 사유. 어떤 문구로 보여줄지는 View가 결정
enum class UserMessage {
    EMPTY_NAME,
    DUPLICATE_NAME
}

// View가 사용자 관리 화면을 그리는 데 필요한 상태 스냅샷
data class UserUiState(
    val users: List<UserEntity>,
    val message: UserMessage?
)

// 사용자 목록 조회·추가를 담당하는 Controller (GameController와 같은 구조)
class UserController(
    private val repository: OmokRepository,
    private val scope: CoroutineScope,
) {

    var uiState by mutableStateOf(UserUiState(users = emptyList(), message = null))
        private set

    // DB의 사용자 목록을 읽어 화면 상태에 반영한다
    fun loadUsers() {
        scope.launch {
            uiState = uiState.copy(users = repository.getUsers())
        }
    }

    // 추가 버튼: 빈 이름·중복 이름을 거르고 저장 후 목록을 새로 읽는다
    fun onAddUser(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            uiState = uiState.copy(message = UserMessage.EMPTY_NAME)
            return
        }
        scope.launch {
            val existingUser = repository.findUserByName(trimmedName)
            if (existingUser != null) {
                uiState = uiState.copy(message = UserMessage.DUPLICATE_NAME)
                return@launch
            }
            repository.addUser(trimmedName)
            uiState = UserUiState(users = repository.getUsers(), message = null)
        }
    }
}
