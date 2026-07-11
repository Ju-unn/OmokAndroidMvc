package com.example.myapplication.omok.domain

class Row private constructor(private val number: Int) {

    companion object {
        private const val FIRST = 1
        private const val LAST = 15

        // 숫자(1~15)로 Row 생성, 범위 초과 시 예외
        fun from(number: Int): Row {
            // 1~15 범위를 벗어난 숫자는 유효하지 않으므로 즉시 예외 발생
            require(number in FIRST..LAST) { "행은 $FIRST ~ $LAST 범위여야 합니다: $number" }
            return Row(number)
        }

        // 0-based 인덱스로 Row 생성
        fun fromIndex(index: Int): Row = from(FIRST + index)

        // 숫자가 유효한 행(1~15)인지 확인
        fun isValidNumber(number: Int): Boolean = number in FIRST..LAST

        // 1~15 전체 행 목록 반환
        fun all(): List<Row> = (FIRST..LAST).map { number -> Row(number) }
    }

    // 0-based 인덱스로 변환 (1=0, 2=1, ...)
    fun toIndex(): Int = number - FIRST

    // 화면 출력용 문자열 반환
    fun display(): String = number.toString()

    // 첫 번째 행(1)인지 확인
    fun isFirst(): Boolean = number == FIRST

    // 마지막 행(15)인지 확인
    fun isLast(): Boolean = number == LAST
}
