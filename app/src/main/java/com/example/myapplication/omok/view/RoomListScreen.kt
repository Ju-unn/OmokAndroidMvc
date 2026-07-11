package com.example.myapplication.omok.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.omok.controller.RoomListController
import com.example.myapplication.omok.controller.RoomMessage
import com.example.myapplication.omok.data.GameRoomEntity
import com.example.myapplication.omok.data.UserEntity

// 방 목록 화면: 방 만들기(제목 + 흑·백 사용자 선택) + 방 목록 + 입장
@Composable
fun RoomListScreen(
    controller: RoomListController,
    onRoomEnterClick: (Long) -> Unit,
    onRecordsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = controller.uiState
    // 만들기 폼의 입력값들은 아직 화면만의 상태라 View가 직접 들고 있는다
    var titleInput by remember { mutableStateOf("") }
    var selectedBlackId by remember { mutableStateOf<Long?>(null) }
    var selectedWhiteId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "게임방",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRecordsClick) {
                Text("전적 보기")
            }
        }
        OutlinedTextField(
            value = titleInput,
            onValueChange = { newValue -> titleInput = newValue },
            label = { Text("방 제목") },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "흑돌 사용자",
            style = MaterialTheme.typography.bodyMedium
        )
        UserChoiceRow(
            users = state.users,
            selectedId = selectedBlackId,
            onSelect = { userId -> selectedBlackId = userId }
        )
        Text(
            text = "백돌 사용자",
            style = MaterialTheme.typography.bodyMedium
        )
        UserChoiceRow(
            users = state.users,
            selectedId = selectedWhiteId,
            onSelect = { userId -> selectedWhiteId = userId }
        )
        Button(
            onClick = {
                controller.onCreateRoom(titleInput, selectedBlackId, selectedWhiteId)
                titleInput = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("방 만들기")
        }
        Text(
            text = state.message?.let { message -> messageText(message) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.rooms) { room ->
                RoomItem(
                    room = room,
                    onEnterClick = { onRoomEnterClick(room.id) }
                )
            }
        }
    }
}

// 흑돌/백돌을 맡을 사용자를 고르는 가로 선택줄
@Composable
private fun UserChoiceRow(
    users: List<UserEntity>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(users) { user ->
            FilterChip(
                selected = user.id == selectedId,
                onClick = { onSelect(user.id) },
                label = { Text(user.name) }
            )
        }
    }
}

// 방 한 줄: 제목 + 상태 + 입장 버튼
@Composable
private fun RoomItem(
    room: GameRoomEntity,
    onEnterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = room.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = statusText(room),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Button(onClick = onEnterClick) {
            Text("입장")
        }
    }
    HorizontalDivider()
}

// 방 상태를 화면 문구로 변환 (문구 선택은 View의 책임)
private fun statusText(room: GameRoomEntity): String {
    if (room.status == GameRoomEntity.STATUS_FINISHED) {
        return "종료 · ${winnerText(room.winnerStone)} 승리"
    }
    return "진행중"
}

private fun winnerText(winnerStone: String?): String {
    if (winnerStone == GameRoomEntity.STONE_BLACK) return "흑"
    return "백"
}

// 방 만들기 실패 사유를 화면 문구로 변환
private fun messageText(message: RoomMessage): String = when (message) {
    RoomMessage.EMPTY_TITLE -> "방 제목을 입력해 주세요."
    RoomMessage.PLAYER_NOT_SELECTED -> "흑돌과 백돌 사용자를 모두 선택해 주세요."
    RoomMessage.SAME_PLAYER -> "흑돌과 백돌은 서로 다른 사용자여야 합니다."
}
