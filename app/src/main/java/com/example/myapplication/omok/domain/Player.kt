package com.example.myapplication.omok.domain

class Player private constructor(private val stone: Stone) {

    companion object {
        // 흑 플레이어 생성
        fun black(): Player = Player(Stone.BLACK)

        // 백 플레이어 생성
        fun white(): Player = Player(Stone.WHITE)
    }

    // 보드의 해당 위치에 이 플레이어의 돌 배치
    fun place(board: Board, position: Position) {
        board.place(position, stone)
    }

    // 주어진 돌이 이 플레이어의 돌과 같은지 확인
    fun isSameStone(other: Stone): Boolean = stone == other

    // 이 플레이어의 돌 종류 반환
    fun stone(): Stone = stone
}
