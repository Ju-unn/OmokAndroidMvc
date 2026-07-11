package com.example.myapplication.omok.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.myapplication.omok.data.GameRoomEntity
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.domain.Column
import com.example.myapplication.omok.domain.OmokGame
import com.example.myapplication.omok.domain.Position
import com.example.myapplication.omok.domain.Row
import com.example.myapplication.omok.domain.Stone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
class GameController(
    private val repository: OmokRepository,
    // suspend 함수(저장)를 실행할 코루틴 작업장. MainActivity가 lifecycleScope를 넘겨준다
    private val scope: CoroutineScope,
) {

    private var game = OmokGame.create()

    // 저장 대상 방. null이면 저장 없이 게임만 진행된다 (방 연결 전에도 동작하도록)
    private var roomId: Long? = null

    // View가 관찰하는 상태. 이 값이 바뀌면 Compose가 화면을 다시 그린다
    var uiState by mutableStateOf(createUiState(lastPosition = null, winner = null, message = null))
        private set

    // 저장 대상 방을 지정하고, 저장된 수순이 있으면 재생해서 보드를 복원한다.
    // 보드를 통째로 저장하지 않아도, 같은 수를 같은 순서로 다시 두면 같은 보드가 된다.
    fun attachRoom(id: Long) {
        roomId = id
        scope.launch {
            val moves = repository.getMovesInOrder(id)
            // 빈 방이면 빈 보드가 그대로 결과가 된다 (다른 방에서 넘어와도 보드가 초기화되도록)
            val restored = OmokGame.create()
            var lastPosition: Position? = null
            var winner: Stone? = null
            moves.forEach { move ->
                val position = Position.from(move.position)
                val stone = restored.currentStone()
                val won = restored.placeCurrentStone(position)
                lastPosition = position
                if (won) winner = stone
            }
            game = restored
            uiState = createUiState(lastPosition = lastPosition, winner = winner, message = null)
        }
    }

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
        // 도메인이 착수를 승인한 뒤에만 저장한다 (실패한 착수는 기록하지 않는다)
        saveMove(position, won, stone)
        uiState = createUiState(
            lastPosition = position,
            winner = if (won) stone else null,
            message = null
        )
    }

    // 다시 시작: 새 게임을 만들고 상태 초기화 + 방의 저장 기록도 함께 초기화
    fun onRestartClick() {
        game = OmokGame.create()
        uiState = createUiState(lastPosition = null, winner = null, message = null)
        roomId?.let { id ->
            scope.launch { repository.restartRoom(id) }
        }
    }

    // 착수 한 수를 DB에 저장하고, 승리로 끝났다면 방 상태를 '종료'로 갱신
    private fun saveMove(position: Position, won: Boolean, stone: Stone) {
        val id = roomId ?: return
        scope.launch {
            repository.saveMove(id, position.display())
            if (won) {
                val winnerStone =
                    if (stone == Stone.BLACK) GameRoomEntity.STONE_BLACK
                    else GameRoomEntity.STONE_WHITE
                repository.finishRoom(id, winnerStone)
            }
        }
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
