package com.example.myapplication.omok.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.omok.controller.UserController
import com.example.myapplication.omok.controller.UserMessage

// 사용자 관리 화면: 이름 입력 + 추가 버튼 + 사용자 목록 + 게임 시작
@Composable
fun UserScreen(
    controller: UserController,
    onRoomListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = controller.uiState
    // 입력 중인 이름은 아직 도메인과 무관한 "화면만의 상태"라 View가 직접 들고 있는다
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "사용자 관리",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nameInput,
                onValueChange = { newValue -> nameInput = newValue },
                label = { Text("이름") },
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                controller.onAddUser(nameInput)
                nameInput = ""
            }) {
                Text("추가")
            }
        }
        Text(
            text = state.message?.let { message -> messageText(message) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        // LazyColumn: 화면에 보이는 줄만 그리는 목록 (사용자가 많아져도 느려지지 않는다)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.users) { user ->
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                HorizontalDivider()
            }
        }
        Button(
            onClick = onRoomListClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("방 목록으로")
        }
    }
}

// 사용자 추가 실패 사유를 화면 문구로 변환 (문구 선택은 View의 책임)
private fun messageText(message: UserMessage): String = when (message) {
    UserMessage.EMPTY_NAME -> "이름을 입력해 주세요."
    UserMessage.DUPLICATE_NAME -> "이미 있는 이름입니다."
}
