package com.example.myapplication.omok.domain

class Players private constructor(
    private val players: List<Player>,
    private val lastPosition: Position? = null
) {

    companion object {
        // 흑 선공으로 Players 생성
        fun create(): Players = Players(listOf(Player.black(), Player.white()))
    }

    // 현재 플레이어를 뒤로 보내 차례를 넘긴 새 Players 반환
    fun nextTurn(lastPosition: Position): Players {
        // 첫 번째 플레이어를 리스트 맨 뒤로 이동해 순환
        val remaining = players.drop(1)
        val first = players.take(1)
        return Players(remaining + first, lastPosition)
    }

    // 첫 번째 차례인지 확인 (아직 둔 돌이 없음)
    fun isFirstTurn(): Boolean = lastPosition == null

    // 마지막 돌 위치 표시 문자열 반환
    fun lastPositionDisplay(): String = lastPosition!!.display()

    // 현재 플레이어의 돌 종류 반환
    fun currentStone(): Stone {
        val current = players.first()
        return current.stone()
    }

    // 현재 플레이어의 돌을 보드에 배치
    fun placeCurrentStone(board: Board, position: Position) {
        val current = players.first()
        current.place(board, position)
    }

    // 현재 플레이어의 돌 종류가 주어진 돌과 같은지 확인
    fun isCurrentPlayerStone(stone: Stone): Boolean {
        val current = players.first()
        return current.isSameStone(stone)
    }

    // 현재 차례가 흑인지 확인
    fun isBlackTurn(): Boolean = isCurrentPlayerStone(Stone.BLACK)
}
