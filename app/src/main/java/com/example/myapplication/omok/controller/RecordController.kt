package com.example.myapplication.omok.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.data.UserRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// View가 전적 화면을 그리는 데 필요한 상태 스냅샷
data class RecordUiState(
    val records: List<UserRecord>
)

// 사용자별 전적 조회를 담당하는 Controller. 이벤트가 없어 조회 함수 하나뿐이다
class RecordController(
    private val repository: OmokRepository,
    private val scope: CoroutineScope,
) {

    var uiState by mutableStateOf(RecordUiState(records = emptyList()))
        private set

    fun loadRecords() {
        scope.launch {
            uiState = RecordUiState(records = repository.getUserRecords())
        }
    }
}
