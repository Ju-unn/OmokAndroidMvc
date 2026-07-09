package org.example.view

import org.example.domain.Column
import org.example.domain.OmokGame
import org.example.domain.Position
import org.example.domain.Row

class BoardRenderer private constructor(
    private val game: OmokGame,
    private val forbiddenPositions: List<Position>
) {

    companion object {
        // 게임 상태로 BoardRenderer 생성, 금수 X 표시 여부는 도메인이 판단 (흑 차례에만 목록 반환)
        fun of(game: OmokGame): BoardRenderer = BoardRenderer(game, game.forbiddenPositions())
    }

    // 보드 전체를 문자열로 렌더링
    fun render(): String {
        val allRows = Row.all()
        // 15행이 위쪽에 오도록 역순 정렬
        val rows = allRows.reversed()
        val rowLines = rows.map { row -> renderRow(row) }
        return rowLines.joinToString("\n") + "\n" + columnLabels()
    }

    // 한 행 전체를 문자열로 렌더링
    private fun renderRow(row: Row): String {
        val columns = Column.all()
        val cells = columns.map { column -> cellSymbolWithConnector(row, column) }
        return renderRowLabel(row) + cells.joinToString("")
    }

    // 행 번호 레이블 반환 (우측 정렬)
    private fun renderRowLabel(row: Row): String = row.display().padStart(3) + " "

    // 셀 기호에 연결선(──) 추가, 마지막 열은 연결선 없음
    private fun cellSymbolWithConnector(row: Row, column: Column): String {
        val symbol = cellSymbol(row, column)
        // 마지막 열 오른쪽에는 연결선이 없음
        if (column.isLast()) return symbol
        return symbol + "──"
    }

    // 해당 위치의 돌 기호, 금수 기호(X), 또는 교차점 기호 반환
    private fun cellSymbol(row: Row, column: Column): String {
        val position = Position.of(column, row)
        val stone = game.stoneAt(position)
        if (!stone.isEmpty()) return stone.symbol()
        if (forbiddenPositions.contains(position)) return "X"
        return borderSymbol(row, column)
    }

    // 보드 가장자리/교차점 기호 반환 (┌┐└┘┬┴├┤┼)
    private fun borderSymbol(row: Row, column: Column): String {
        // 네 모서리 먼저 판별
        if (row.isLast() && column.isFirst()) return "┌"
        if (row.isLast() && column.isLast()) return "┐"
        if (row.isFirst() && column.isFirst()) return "└"
        if (row.isFirst() && column.isLast()) return "┘"
        // 위/아래 가장자리 (모서리 제외)
        if (row.isLast()) return "┬"
        if (row.isFirst()) return "┴"
        // 좌/우 가장자리 (모서리 제외)
        if (column.isFirst()) return "├"
        if (column.isLast()) return "┤"
        // 내부 교차점
        return "┼"
    }

    // 열 레이블(A B C ... O) 문자열 반환
    private fun columnLabels(): String {
        val columns = Column.all()
        return "    " + columns.joinToString("  ") { column -> column.display() }
    }
}
