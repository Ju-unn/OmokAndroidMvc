package org.example.domain

// 보드와 진행 상태를 묶어 게임 전체 규칙을 담당하는 도메인 루트
class OmokGame private constructor(
    private val board: Board,
    private var progress: GameProgress
) {

    companion object {
        // 빈 보드, 흑 선공으로 새 게임 생성
        fun create(): OmokGame = OmokGame(Board.create(), GameProgress.start())
    }

    // 게임이 끝났는지 확인
    fun isFinished(): Boolean = progress.isFinished()

    // 첫 번째 차례인지 확인
    fun isFirstTurn(): Boolean = progress.isFirstTurn()

    // 현재 플레이어의 돌 종류 반환
    fun currentStone(): Stone = progress.currentStone()

    // 마지막 돌 위치 표시 문자열 반환
    fun lastPositionDisplay(): String = progress.lastPositionDisplay()

    // 해당 위치에 이미 돌이 있는지 확인
    fun isOccupied(position: Position): Boolean = board.hasStoneAt(position)

    // 해당 위치의 돌 반환
    fun stoneAt(position: Position): Stone = board.stoneAt(position)

    // 현재 차례가 흑일 때만 렌주룰 금수 여부 검사 (백은 금수 없음)
    fun isForbiddenNow(position: Position): Boolean {
        if (!progress.isBlackTurn()) return false
        return ForbiddenChecker.of(board).isForbidden(position)
    }

    // 현재 플레이어의 돌을 놓고 승리 여부 반환, 승리면 종료·아니면 차례 전환
    fun placeCurrentStone(position: Position): Boolean {
        progress.placeCurrentStone(board, position)
        val won = WinChecker.of(board).isWin(position, board.stoneAt(position))
        progress = progress.next(position, won)
        return won
    }

    // 흑 차례일 때 금수 위치 목록 반환, 그 외에는 빈 목록
    fun forbiddenPositions(): List<Position> {
        if (progress.isFinished()) return emptyList()
        if (!progress.isBlackTurn()) return emptyList()
        return ForbiddenChecker.of(board).forbiddenPositions()
    }
}
