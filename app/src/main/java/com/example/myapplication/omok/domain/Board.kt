package com.example.myapplication.omok.domain

class Board private constructor(private val stones: Stones) {

    companion object {
        // 보드 한 변의 크기, 다른 도메인 클래스에서도 참조하는 단일 기준
        const val BOARD_SIZE = 15

        // 빈 15x15 보드 생성
        fun create(): Board = Board(Stones.empty())
    }

    // 해당 위치의 돌 반환
    fun stoneAt(position: Position): Stone = stones.stoneAt(position)

    // 해당 위치에 돌 배치, 범위 초과 또는 이미 돌이 있으면 예외
    fun place(position: Position, stone: Stone) {
        // 보드 밖 위치에는 착수 불가
        require(isValidPosition(position)) { "보드 범위를 벗어난 위치입니다: ${position.display()}" }
        // 이미 돌이 있는 위치에 중복 착수 불가
        require(!hasStoneAt(position)) { "이미 돌이 있는 위치입니다: ${position.display()}" }
        stones.place(position, stone)
    }

    // 해당 위치가 보드 범위 안인지 확인
    fun isValidPosition(position: Position): Boolean {
        val columnInRange = position.columnIndex() in 0 until BOARD_SIZE
        val rowInRange = position.rowIndex() in 0 until BOARD_SIZE
        return columnInRange && rowInRange
    }

    // 해당 위치에 돌이 있는지 확인
    fun hasStoneAt(position: Position): Boolean {
        val stone = stoneAt(position)
        return !stone.isEmpty()
    }

}
