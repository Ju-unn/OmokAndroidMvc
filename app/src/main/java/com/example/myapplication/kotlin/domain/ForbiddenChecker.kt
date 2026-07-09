package org.example.domain

class ForbiddenChecker private constructor(private val board: Board) {

    companion object {
        private const val OVERLINE_COUNT = 6
        private const val FOUR_COUNT = 4
        private val DIRECTIONS = listOf(Pair(1, 0), Pair(0, 1), Pair(1, 1), Pair(1, -1))

        fun of(board: Board): ForbiddenChecker = ForbiddenChecker(board)
    }

    // 장목 판정: 흑이 해당 위치에 착수 후 6개 이상 연속이 만들어지면 true
    fun isOverline(position: Position): Boolean {
        val winChecker = WinChecker.of(board)
        if (winChecker.countHorizontal(position, Stone.BLACK) >= OVERLINE_COUNT) return true
        if (winChecker.countVertical(position, Stone.BLACK) >= OVERLINE_COUNT) return true
        if (winChecker.countDiagonalUpRight(position, Stone.BLACK) >= OVERLINE_COUNT) return true
        return winChecker.countDiagonalDownRight(position, Stone.BLACK) >= OVERLINE_COUNT
    }

    // 4-4 판정: 흑이 해당 위치에 착수 후 4목 형태가 두 방향 이상에서 동시에 만들어지면 true
    fun isDoubleFour(position: Position): Boolean {
        val fourCount = DIRECTIONS.count { (deltaColumn, deltaRow) ->
            isFourInDirection(position, deltaColumn, deltaRow)
        }
        return fourCount >= 2
    }

    // 3-3 판정: 흑이 해당 위치에 착수 후 열린 3이 두 방향 이상에서 동시에 만들어지면 true
    fun isDoubleThree(position: Position): Boolean = countOpenThrees(position) >= 2

    // 금수 통합 판정: 정확히 5목이 동시에 만들어지면 금수 아님, 장목·4-4·3-3 중 하나라도 해당하면 true
    fun isForbidden(position: Position): Boolean {
        val winChecker = WinChecker.of(board)
        if (winChecker.isWin(position, Stone.BLACK)) return false
        return isOverline(position) || isDoubleFour(position) || isDoubleThree(position)
    }

    // 보드 위 빈 칸 중 흑이 착수하면 금수가 되는 모든 위치 목록 반환
    fun forbiddenPositions(): List<Position> {
        val columns = Column.all()
        val allPositions = columns.flatMap { column ->
            val rows = Row.all()
            rows.map { row -> Position.of(column, row) }
        }
        return allPositions.filter { position ->
            !board.hasStoneAt(position) && isForbidden(position)
        }
    }

    // 열린 3 개수 반환: 흑이 해당 위치에 착수 후 만들어진 열린 3의 방향 수 반환 (3-3 판정에서 사용)
    fun countOpenThrees(position: Position): Int =
        DIRECTIONS.count { (deltaColumn, deltaRow) ->
            isOpenThreeInDirection(position, deltaColumn, deltaRow)
        }

    // 해당 방향(축)에서 정확히 4개 연속이고 한쪽 이상이 열려있으면 true
    private fun isFourInDirection(position: Position, deltaColumn: Int, deltaRow: Int): Boolean {
        val forwardCount = countInDirection(position, deltaColumn, deltaRow)
        val backwardCount = countInDirection(position, -deltaColumn, -deltaRow)
        if (forwardCount + backwardCount + 1 != FOUR_COUNT) return false
        return isOpenEnd(position, deltaColumn, deltaRow) || isOpenEnd(position, -deltaColumn, -deltaRow)
    }

    // 특정 방향으로 연속된 흑돌 수 반환 (position 자신 제외)
    private fun countInDirection(position: Position, deltaColumn: Int, deltaRow: Int): Int {
        var count = 0
        var columnIndex = position.columnIndex() + deltaColumn
        var rowIndex = position.rowIndex() + deltaRow
        while (isInBounds(columnIndex, rowIndex) && isBlackAt(columnIndex, rowIndex)) {
            count++
            columnIndex += deltaColumn
            rowIndex += deltaRow
        }
        return count
    }

    // 특정 방향으로 연속 끝 너머 칸이 보드 안이고 비어있으면 true
    private fun isOpenEnd(position: Position, deltaColumn: Int, deltaRow: Int): Boolean {
        var columnIndex = position.columnIndex() + deltaColumn
        var rowIndex = position.rowIndex() + deltaRow
        while (isInBounds(columnIndex, rowIndex) && isBlackAt(columnIndex, rowIndex)) {
            columnIndex += deltaColumn
            rowIndex += deltaRow
        }
        return isInBounds(columnIndex, rowIndex) && isEmptyAt(columnIndex, rowIndex)
    }

    // 해당 방향에서 position을 포함하는 4칸 윈도우 중 열린 3 패턴이 하나라도 있으면 true
    private fun isOpenThreeInDirection(position: Position, deltaColumn: Int, deltaRow: Int): Boolean {
        val columnIndex = position.columnIndex()
        val rowIndex = position.rowIndex()
        for (offset in 0..3) {
            val startColumnIndex = columnIndex - offset * deltaColumn
            val startRowIndex = rowIndex - offset * deltaRow
            if (isOpenThreeWindow(startColumnIndex, startRowIndex, deltaColumn, deltaRow, position)) return true
        }
        return false
    }

    // 4칸 윈도우 내에 흑돌 3개·빈칸 1개이고 양쪽 외부가 비어있으면 열린 3 → true
    // 단, 빈칸을 채워 만든 4가 열린 4(양쪽 모두 정확히 5 가능)가 될 수 없으면 거짓 3 → false
    // targetPosition은 아직 보드에 없어도 흑으로 간주 (착수 전 검사 지원)
    private fun isOpenThreeWindow(
        startColumnIndex: Int,
        startRowIndex: Int,
        deltaColumn: Int,
        deltaRow: Int,
        targetPosition: Position
    ): Boolean {
        if ((0..3).any { index ->
                !isInBounds(
                    startColumnIndex + index * deltaColumn,
                    startRowIndex + index * deltaRow
                )
            }) return false
        val blackCount = (0..3).count { index ->
            isBlackOrTarget(
                startColumnIndex + index * deltaColumn,
                startRowIndex + index * deltaRow,
                targetPosition
            )
        }
        val emptyCount = (0..3).count { index ->
            isEmptyExcludingTarget(
                startColumnIndex + index * deltaColumn,
                startRowIndex + index * deltaRow,
                targetPosition
            )
        }
        if (blackCount != 3 || emptyCount != 1) return false
        val leftOpen = isInBounds(startColumnIndex - deltaColumn, startRowIndex - deltaRow) &&
                isEmptyAt(startColumnIndex - deltaColumn, startRowIndex - deltaRow)
        val rightOpen = isInBounds(startColumnIndex + 4 * deltaColumn, startRowIndex + 4 * deltaRow) &&
                isEmptyAt(startColumnIndex + 4 * deltaColumn, startRowIndex + 4 * deltaRow)
        if (!leftOpen || !rightOpen) return false
        val leftOverline = isBlackBeyond(startColumnIndex - 2 * deltaColumn, startRowIndex - 2 * deltaRow)
        val rightOverline = isBlackBeyond(startColumnIndex + 5 * deltaColumn, startRowIndex + 5 * deltaRow)
        return !leftOverline && !rightOverline
    }

    // 열린 끝 너머 칸에 흑돌이 있는지 확인: 있으면 그쪽 끝에서 5를 만들 때 6목(장목)이 되어 열린 4로 발전 불가
    private fun isBlackBeyond(columnIndex: Int, rowIndex: Int): Boolean =
        isInBounds(columnIndex, rowIndex) && isBlackAt(columnIndex, rowIndex)

    // 해당 칸이 흑돌이거나 targetPosition이면 true (착수 전 검사를 위해 targetPosition을 흑으로 간주)
    private fun isBlackOrTarget(columnIndex: Int, rowIndex: Int, targetPosition: Position): Boolean {
        if (columnIndex == targetPosition.columnIndex() && rowIndex == targetPosition.rowIndex()) return true
        return isBlackAt(columnIndex, rowIndex)
    }

    // 해당 칸이 비어있고 targetPosition이 아니면 true (targetPosition은 착수 예정이므로 비어있지 않은 것으로 간주)
    private fun isEmptyExcludingTarget(columnIndex: Int, rowIndex: Int, targetPosition: Position): Boolean {
        if (columnIndex == targetPosition.columnIndex() && rowIndex == targetPosition.rowIndex()) return false
        return isEmptyAt(columnIndex, rowIndex)
    }

    // 인덱스가 보드 범위 안인지 확인
    private fun isInBounds(columnIndex: Int, rowIndex: Int): Boolean =
        columnIndex in 0 until Board.BOARD_SIZE && rowIndex in 0 until Board.BOARD_SIZE

    // 해당 인덱스 위치에 흑돌이 있는지 확인
    private fun isBlackAt(columnIndex: Int, rowIndex: Int): Boolean {
        val targetPosition = Position.of(Column.fromIndex(columnIndex), Row.fromIndex(rowIndex))
        return board.stoneAt(targetPosition) == Stone.BLACK
    }

    // 해당 인덱스 위치가 비어있는지 확인
    private fun isEmptyAt(columnIndex: Int, rowIndex: Int): Boolean {
        val targetPosition = Position.of(Column.fromIndex(columnIndex), Row.fromIndex(rowIndex))
        return board.stoneAt(targetPosition).isEmpty()
    }
}
