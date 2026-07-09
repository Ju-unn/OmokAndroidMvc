package org.example.domain

class WinChecker private constructor(private val board: Board) {

    companion object {
        private const val WIN_COUNT = 5

        // 보드를 기반으로 WinChecker 생성
        fun of(board: Board): WinChecker = WinChecker(board)
    }

    // 가로 방향 연속 돌 수 반환 (좌우 합산 + 자신)
    fun countHorizontal(position: Position, stone: Stone): Int {
        val right = countInDirection(position, stone, 1, 0)
        val left = countInDirection(position, stone, -1, 0)
        return right + left + 1
    }

    // 세로 방향 연속 돌 수 반환 (상하 합산 + 자신)
    fun countVertical(position: Position, stone: Stone): Int {
        val up = countInDirection(position, stone, 0, 1)
        val down = countInDirection(position, stone, 0, -1)
        return up + down + 1
    }

    // 우상향 대각선(/) 연속 돌 수 반환
    fun countDiagonalUpRight(position: Position, stone: Stone): Int {
        val upRight = countInDirection(position, stone, 1, 1)
        val downLeft = countInDirection(position, stone, -1, -1)
        return upRight + downLeft + 1
    }

    // 우하향 대각선(\) 연속 돌 수 반환
    fun countDiagonalDownRight(position: Position, stone: Stone): Int {
        val downRight = countInDirection(position, stone, 1, -1)
        val upLeft = countInDirection(position, stone, -1, 1)
        return downRight + upLeft + 1
    }

    // 흑은 정확히 5목, 백은 5목 이상이면 승리 (렌주룰)
    fun isWin(position: Position, stone: Stone): Boolean {
        if (stone == Stone.BLACK) return isExactlyFive(position, stone)
        return isFiveOrMore(position, stone)
    }

    // 4방향 중 하나라도 정확히 5개 연속이면 true (흑 전용)
    private fun isExactlyFive(position: Position, stone: Stone): Boolean {
        if (countHorizontal(position, stone) == WIN_COUNT) return true
        if (countVertical(position, stone) == WIN_COUNT) return true
        if (countDiagonalUpRight(position, stone) == WIN_COUNT) return true
        return countDiagonalDownRight(position, stone) == WIN_COUNT
    }

    // 4방향 중 하나라도 5개 이상 연속이면 true (백 전용)
    private fun isFiveOrMore(position: Position, stone: Stone): Boolean {
        if (countHorizontal(position, stone) >= WIN_COUNT) return true
        if (countVertical(position, stone) >= WIN_COUNT) return true
        if (countDiagonalUpRight(position, stone) >= WIN_COUNT) return true
        return countDiagonalDownRight(position, stone) >= WIN_COUNT
    }

    // 지정 방향으로 연속된 같은 돌 수 반환 (경계 또는 다른 색 돌에서 중단)
    private fun countInDirection(position: Position, stone: Stone, deltaColumn: Int, deltaRow: Int): Int {
        var count = 0
        // 현재 위치 다음 칸부터 탐색 시작
        var columnIndex = position.columnIndex() + deltaColumn
        var rowIndex = position.rowIndex() + deltaRow
        // 보드 범위 안이고 같은 색 돌이 계속되는 동안 반복
        while (isInBounds(columnIndex, rowIndex) && matchesStone(columnIndex, rowIndex, stone)) {
            count++
            columnIndex += deltaColumn
            rowIndex += deltaRow
        }
        return count
    }

    // 인덱스가 보드 범위 안인지 확인
    private fun isInBounds(columnIndex: Int, rowIndex: Int): Boolean =
        columnIndex in 0 until Board.BOARD_SIZE && rowIndex in 0 until Board.BOARD_SIZE

    // 해당 인덱스 위치의 돌이 주어진 돌과 같은지 확인
    private fun matchesStone(columnIndex: Int, rowIndex: Int, stone: Stone): Boolean {
        val column = Column.fromIndex(columnIndex)
        val row = Row.fromIndex(rowIndex)
        val position = Position.of(column, row)
        return board.stoneAt(position) == stone
    }
}
