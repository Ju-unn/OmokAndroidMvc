package org.example

import org.example.controller.Game
import org.example.view.InputView
import org.example.view.OutputView

// 게임 진입점: InputView, OutputView, Game을 생성하고 실행
fun main() {
    val inputView = InputView.create()
    val outputView = OutputView.create()
    val game = Game.of(inputView, outputView)
    game.run()
}
