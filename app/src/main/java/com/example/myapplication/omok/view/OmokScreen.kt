package com.example.myapplication.omok.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.myapplication.omok.controller.GameController
import com.example.myapplication.omok.controller.GameMessage
import com.example.myapplication.omok.controller.GameUiState
import com.example.myapplication.omok.domain.Board
import com.example.myapplication.omok.domain.Position
import com.example.myapplication.omok.domain.Stone
import com.example.myapplication.omok.domain.Column as BoardColumn
import com.example.myapplication.omok.domain.Row as BoardRow

// 보드 배경(나무) 색상
private val BoardBackground = Color(0xFFD9A55C)

// 오목 게임 전체 화면: 상태 안내 + 보드 + 다시 시작 버튼
@Composable
fun OmokScreen(controller: GameController, modifier: Modifier = Modifier) {
    val state = controller.uiState
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = statusText(state),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = state.message?.let { messageText(it) } ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        BoardCanvas(
            state = state,
            onCellClick = controller::onCellClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
        Button(onClick = controller::onRestartClick) {
            Text("다시 시작")
        }
    }
}

// 승리 또는 현재 차례 안내 문구 (콘솔 OutputView의 메시지 역할)
private fun statusText(state: GameUiState): String {
    val winner = state.winner
    if (winner != null) return "${winner.stoneName()} 승리! 축하합니다!"
    return "${state.currentStone.stoneName()}의 차례입니다."
}

// 착수 실패 사유를 화면 문구로 변환 (문구 선택은 View의 책임)
private fun messageText(message: GameMessage): String = when (message) {
    GameMessage.OCCUPIED -> "이미 돌이 있는 위치입니다."
    GameMessage.FORBIDDEN -> "렌주룰에 의해 그 자리에 돌을 놓을 수 없습니다."
}

// 돌 종류에 따른 이름 반환 (흑/백)
private fun Stone.stoneName(): String = when (this) {
    Stone.BLACK -> "흑"
    Stone.WHITE -> "백"
    Stone.EMPTY -> ""
}

// 15x15 보드를 그리고 터치를 Position으로 변환해 전달하는 Canvas
@Composable
private fun BoardCanvas(
    state: GameUiState,
    onCellClick: (Position) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .background(BoardBackground)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // 터치 좌표를 가장 가까운 교차점의 열/행 인덱스로 변환
                    val cell = size.width / Board.BOARD_SIZE.toFloat()
                    val columnIndex = (offset.x / cell).toInt()
                        .coerceIn(0, Board.BOARD_SIZE - 1)
                    val rowIndexFromTop = (offset.y / cell).toInt()
                        .coerceIn(0, Board.BOARD_SIZE - 1)
                    // 화면 위쪽이 15행이므로 위에서부터 센 인덱스를 뒤집음
                    val rowIndex = Board.BOARD_SIZE - 1 - rowIndexFromTop
                    val position = Position.of(
                        BoardColumn.fromIndex(columnIndex),
                        BoardRow.fromIndex(rowIndex)
                    )
                    onCellClick(position)
                }
            }
    ) {
        val cell = size.width / Board.BOARD_SIZE
        drawGridLines(cell)
        // 금수 X 표시 (표시 여부는 도메인이 판단한 목록을 그대로 그림)
        state.forbiddenPositions.forEach { position ->
            drawForbiddenMark(centerOf(position, cell), cell)
        }
        state.stones.forEach { (position, stone) ->
            drawStone(centerOf(position, cell), stone, cell)
        }
        // 마지막 돌 위치 표시 (콘솔의 "마지막 돌의 위치" 안내 역할)
        state.lastPosition?.let { position ->
            drawCircle(
                color = Color.Red,
                radius = cell * 0.12f,
                center = centerOf(position, cell)
            )
        }
    }
}

// Position을 화면 좌표(교차점 중심)로 변환
private fun centerOf(position: Position, cell: Float): Offset {
    val half = cell / 2
    val x = half + position.columnIndex() * cell
    // 15행이 위쪽에 오도록 행 인덱스를 뒤집음
    val y = half + (Board.BOARD_SIZE - 1 - position.rowIndex()) * cell
    return Offset(x, y)
}

// 보드 격자선 그리기
private fun DrawScope.drawGridLines(cell: Float) {
    val half = cell / 2
    val end = cell * Board.BOARD_SIZE - half
    for (index in 0 until Board.BOARD_SIZE) {
        val offset = half + index * cell
        drawLine(Color.Black, Offset(half, offset), Offset(end, offset), strokeWidth = 2f)
        drawLine(Color.Black, Offset(offset, half), Offset(offset, end), strokeWidth = 2f)
    }
}

// 돌 그리기 (흰 돌은 테두리로 구분)
private fun DrawScope.drawStone(center: Offset, stone: Stone, cell: Float) {
    val radius = cell * 0.42f
    val color = if (stone == Stone.BLACK) Color.Black else Color.White
    drawCircle(color, radius, center)
    drawCircle(Color.Black, radius, center, style = Stroke(width = 2f))
}

// 금수 위치 X 표시 그리기
private fun DrawScope.drawForbiddenMark(center: Offset, cell: Float) {
    val reach = cell * 0.25f
    drawLine(
        Color.Red,
        Offset(center.x - reach, center.y - reach),
        Offset(center.x + reach, center.y + reach),
        strokeWidth = 4f
    )
    drawLine(
        Color.Red,
        Offset(center.x - reach, center.y + reach),
        Offset(center.x + reach, center.y - reach),
        strokeWidth = 4f
    )
}
