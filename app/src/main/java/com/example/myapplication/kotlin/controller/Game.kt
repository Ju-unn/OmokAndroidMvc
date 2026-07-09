package org.example.controller

import org.example.domain.OmokGame
import org.example.domain.Position
import org.example.view.BoardRenderer
import org.example.view.InputView
import org.example.view.OutputView

// View의 입력을 Domain(OmokGame)에 전달하고 판단 결과만 받아 View에 전달하는 Controller
class Game private constructor(
    private val inputView: InputView,
    private val outputView: OutputView
) {

    companion object {
        // InputView, OutputView로 Game 생성
        fun of(inputView: InputView, outputView: OutputView): Game = Game(inputView, outputView)
    }

    // 빈 보드와 흑 선공으로 게임 시작
    fun run() {
        playGame(OmokGame.create())
    }

    // 보드 출력 → 유효한 위치 입력 → 착수 → 승리 여부에 따라 종료 또는 다음 턴 반복
    private fun playGame(game: OmokGame) {
        // 현재 보드 출력 (금수 X 표시 여부는 도메인이 판단)
        showBoard(game)
        // "흑/백의 차례입니다" 안내 메시지 출력
        showTurnMessage(game)
        // 올바른 위치가 입력될 때까지 반복해서 입력받기 (형식·점유·금수 검사)
        val position = readValidPosition(game)
        // 착수 후에는 차례가 넘어가므로 현재 돌을 먼저 보관
        val stone = game.currentStone()
        // 착수와 승리 판정은 도메인이 수행, 컨트롤러는 결과만 받음
        val won = game.placeCurrentStone(position)
        if (won) {
            showBoard(game)
            outputView.showWinMessage(stone)
            return
        }
        // 승리가 아니면 다음 턴 진행 (재귀 호출)
        playGame(game)
    }

    // 게임 상태에 따라 차례 메시지 출력
    private fun showTurnMessage(game: OmokGame) {
        val stone = game.currentStone()
        if (game.isFirstTurn()) outputView.showFirstTurnMessage(stone)
        else outputView.showTurnMessage(stone, game.lastPositionDisplay())
    }

    // 현재 게임 상태로 보드 출력
    private fun showBoard(game: OmokGame) {
        outputView.showBoard(BoardRenderer.of(game).render())
    }

    // 유효한 위치가 입력될 때까지 반복해서 읽기 (형식·점유·금수 검사 포함)
    private fun readValidPosition(game: OmokGame): Position {
        while (true) {
            val input = inputView.readInput()
            if (!Position.canParse(input)) {
                outputView.showInvalidFormatMessage()
                continue
            }
            val position = Position.from(input)
            if (game.isOccupied(position)) {
                outputView.showOccupiedMessage()
                continue
            }
            if (game.isForbiddenNow(position)) {
                outputView.showForbiddenMessage()
                continue
            }
            return position
        }
    }
}
