package com.example.myapplication.omok.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.omok.domain.Column
import com.example.myapplication.omok.domain.OmokGame
import com.example.myapplication.omok.domain.Position
import com.example.myapplication.omok.domain.Row
import com.example.myapplication.omok.domain.Stone

// 착수 실패 사유. 어떤 문구로 보여줄지는 View가 결정
enum class GameMessage {
    OCCUPIED,
    FORBIDDEN
}

// View가 화면을 그리는 데 필요한 게임 상태 스냅샷
data class GameUiState(
    val stones: Map<Position, Stone>,
    val forbiddenPositions: List<Position>,
    val currentStone: Stone,
    val lastPosition: Position?,
    val winner: Stone?,
    val message: GameMessage?
)

// View의 터치 이벤트를 Domain(OmokGame)에 전달하고 판단 결과만 받아 UI 상태로 반영하는 Controller
// 콘솔 버전의 "입력 대기 루프" 대신 onCellClick 이벤트가 들어올 때마다 한 턴씩 진행한다
class GameController {

    private var game = OmokGame.create()

    // View가 관찰하는 상태. 이 값이 바뀌면 Compose가 화면을 다시 그린다
    var uiState by mutableStateOf(createUiState(lastPosition = null, winner = null, message = null))
        private set

    // 보드의 교차점을 터치했을 때 호출: 점유·금수 검사 후 착수 (판단은 도메인이 수행)
    fun onCellClick(position: Position) {
        // 게임이 끝났으면 입력 무시
        if (game.isFinished()) return
        if (game.isOccupied(position)) {
            uiState = uiState.copy(message = GameMessage.OCCUPIED)
            return
        }
        if (game.isForbiddenNow(position)) {
            uiState = uiState.copy(message = GameMessage.FORBIDDEN)
            return
        }
        // 착수 후에는 차례가 넘어가므로 현재 돌을 먼저 보관 (승리 표시용)
        val stone = game.currentStone()
        val won = game.placeCurrentStone(position)
        uiState = createUiState(
            lastPosition = position,
            winner = if (won) stone else null,
            message = null
        )
    }

    // 다시 시작: 새 게임을 만들고 상태 초기화
    fun onRestartClick() {
        game = OmokGame.create()
        uiState = createUiState(lastPosition = null, winner = null, message = null)
    }

    // 현재 도메인 상태를 View용 스냅샷으로 변환
    private fun createUiState(
        lastPosition: Position?,
        winner: Stone?,
        message: GameMessage?
    ): GameUiState {
        val stones = mutableMapOf<Position, Stone>()
        Column.all().forEach { column ->
            Row.all().forEach { row ->
                val position = Position.of(column, row)
                val stone = game.stoneAt(position)
                if (!stone.isEmpty()) stones[position] = stone
            }
        }
        return GameUiState(
            stones = stones,
            forbiddenPositions = game.forbiddenPositions(),
            currentStone = game.currentStone(),
            lastPosition = lastPosition,
            winner = winner,
            message = message
        )
    }
}
