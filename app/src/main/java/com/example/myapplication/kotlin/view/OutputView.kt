package org.example.view

import org.example.domain.Stone
import java.io.PrintStream

class OutputView(private val output: PrintStream) {

    companion object {
        // 표준 출력으로 OutputView 생성 (UTF-8 인코딩)
        fun create(): OutputView = OutputView(PrintStream(System.out, true, "UTF-8"))
    }

    // 렌더링된 보드 문자열 출력
    fun showBoard(renderedBoard: String) {
        show(renderedBoard)
    }

    // 첫 차례 안내 메시지 출력
    fun showFirstTurnMessage(stone: Stone) {
        show("${stone.stoneName()}의 차례입니다.")
    }

    // 차례 안내 메시지 출력 (마지막 돌 위치 포함)
    fun showTurnMessage(stone: Stone, lastPositionDisplay: String) {
        show("${stone.stoneName()}의 차례입니다. (마지막 돌의 위치: $lastPositionDisplay)")
    }

    // 승리 메시지 포맷팅 후 출력
    fun showWinMessage(stone: Stone) {
        show("${stone.stoneName()} 승리! 축하합니다!")
    }

    // 잘못된 형식 오류 메시지 출력
    fun showInvalidFormatMessage() {
        show("잘못된 입력입니다. 다시 입력하세요.")
    }

    // 이미 돌이 있는 위치 오류 메시지 출력
    fun showOccupiedMessage() {
        show("이미 돌이 있는 위치입니다. 다시 입력하세요.")
    }

    // 렌주룰 금수 위치 착수 시도 안내 메시지 출력
    fun showForbiddenMessage() {
        show("렌주룰에 의해 그 자리에 돌을 놓을 수 없습니다. 다시 입력하세요.")
    }

    // 줄바꿈과 함께 메시지 출력
    private fun show(message: String) = output.println(message)
}
