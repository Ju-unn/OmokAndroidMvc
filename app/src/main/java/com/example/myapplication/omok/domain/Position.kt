package com.example.myapplication.omok.domain

class Position private constructor(
    private val column: Column,
    private val row: Row
) {

    companion object {
        // Column과 Row로 Position 생성
        fun of(column: Column, row: Row): Position = Position(column, row)

        // "H8" 형식의 문자열을 Position으로 변환, 파싱 실패 시 예외
        fun from(input: String): Position {
            val normalized = input.trim().uppercase()
            // 첫 글자가 열(Column), 나머지가 행(Row) 번호
            val letter = normalized.first()
            val numberText = normalized.drop(1)
            // 행 번호가 숫자가 아니면 null이 되므로 예외 발생
            val number = requireNotNull(numberText.toIntOrNull()) { "행 번호가 올바르지 않습니다: $numberText" }
            return of(Column.from(letter), Row.from(number))
        }

        // 문자열이 유효한 Position 형식인지 확인
        fun canParse(input: String): Boolean {
            val normalized = input.trim().uppercase()
            // 열 + 행 최소 2글자 미만이면 형식 불일치
            if (normalized.length < 2) return false
            val letter = normalized.first()
            val numberText = normalized.drop(1)
            // 행 번호가 숫자가 아니면 파싱 불가
            val number = numberText.toIntOrNull() ?: return false
            // 열 문자와 행 번호가 모두 유효한 범위인지 확인
            return Column.isValidLetter(letter) && Row.isValidNumber(number)
        }
    }

    // 열의 0-based 인덱스 반환
    fun columnIndex(): Int = column.toIndex()

    // 행의 0-based 인덱스 반환
    fun rowIndex(): Int = row.toIndex()

    // 화면 출력용 문자열 반환 (예: "H8")
    fun display(): String = column.display() + row.display()

    override fun equals(other: Any?): Boolean {
        // Position이 아닌 타입은 동등 비교 불가
        if (other !is Position) return false
        // 열과 행 인덱스가 모두 같아야 동일 위치
        return columnIndex() == other.columnIndex() && rowIndex() == other.rowIndex()
    }

    override fun hashCode(): Int = 31 * columnIndex() + rowIndex()
}
