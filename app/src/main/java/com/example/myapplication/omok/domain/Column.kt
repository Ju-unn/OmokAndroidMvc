package com.example.myapplication.omok.domain

class Column private constructor(private val letter: Char) {

    companion object {
        private const val FIRST = 'A'
        private const val LAST = 'O'

        // 문자(A~O)로 Column 생성, 범위 초과 시 예외
        fun from(letter: Char): Column {
            // A~O 범위를 벗어난 문자는 유효하지 않으므로 즉시 예외 발생
            require(letter in FIRST..LAST) { "열은 $FIRST ~ $LAST 범위여야 합니다: $letter" }
            return Column(letter)
        }

        // 0-based 인덱스로 Column 생성
        fun fromIndex(index: Int): Column = from(FIRST + index)

        // 문자가 유효한 열(A~O)인지 확인
        fun isValidLetter(letter: Char): Boolean = letter in FIRST..LAST

        // A~O 전체 열 목록 반환
        fun all(): List<Column> = (FIRST..LAST).map { letter -> Column(letter) }
    }

    // 0-based 인덱스로 변환 (A=0, B=1, ...)
    fun toIndex(): Int = letter - FIRST

    // 화면 출력용 문자열 반환
    fun display(): String = letter.toString()

    // 첫 번째 열(A)인지 확인
    fun isFirst(): Boolean = letter == FIRST

    // 마지막 열(O)인지 확인
    fun isLast(): Boolean = letter == LAST
}
