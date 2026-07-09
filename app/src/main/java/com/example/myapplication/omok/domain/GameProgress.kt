package com.example.myapplication.omok.domain

// 게임의 진행 상태(현재 차례 + 종료 여부)를 관리
class GameProgress private constructor(
    private val players: Players,
    private val finished: Boolean
) {

    companion object {
        // 흑 선공, 진행 중 상태로 시작
        fun start(): GameProgress = GameProgress(Players.create(), false)
    }

    // 게임이 끝났는지 확인
    fun isFinished(): Boolean = finished

    // 첫 번째 차례인지 확인
    fun isFirstTurn(): Boolean = players.isFirstTurn()

    // 현재 차례가 흑인지 확인
    fun isBlackTurn(): Boolean = players.isBlackTurn()

    // 현재 플레이어의 돌 종류 반환
    fun currentStone(): Stone = players.currentStone()

    // 마지막 돌 위치 표시 문자열 반환
    fun lastPositionDisplay(): String = players.lastPositionDisplay()

    // 현재 플레이어의 돌을 보드에 배치
    fun placeCurrentStone(board: Board, position: Position) {
        players.placeCurrentStone(board, position)
    }

    // 승리 시 현재 차례를 유지한 채 종료 상태로, 아니면 다음 차례로 진행
    fun next(lastPosition: Position, won: Boolean): GameProgress {
        if (won) return GameProgress(players, true)
        return GameProgress(players.nextTurn(lastPosition), false)
    }
}
