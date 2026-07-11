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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.omok.controller.RecordController
import com.example.myapplication.omok.data.UserRecord

// 전적 화면: 사용자별 승/패/진행중 표
@Composable
fun RecordScreen(
    controller: RecordController,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = controller.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "전적",
            style = MaterialTheme.typography.titleLarge
        )
        RecordHeaderRow()
        HorizontalDivider()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.records) { record ->
                RecordRow(record = record)
            }
        }
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("방 목록으로")
        }
    }
}

// 표의 제목 줄
@Composable
private fun RecordHeaderRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "이름",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "승",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "패",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "진행중",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
    }
}

// 사용자 한 명의 전적 줄
@Composable
private fun RecordRow(record: UserRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = record.userName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = "${record.wins}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${record.losses}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${record.inProgressCount}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
    HorizontalDivider()
}
