package org.example.view

import org.example.domain.Stone

// 돌 종류에 따른 화면 출력 기호 반환
fun Stone.symbol(): String = when (this) {
    Stone.BLACK -> "○"
    Stone.WHITE -> "●"
    Stone.EMPTY -> " "
}

// 돌 종류에 따른 이름 반환 (흑/백)
fun Stone.stoneName(): String = when (this) {
    Stone.BLACK -> "흑"
    Stone.WHITE -> "백"
    // 빈 칸은 플레이어의 돌이 될 수 없으므로 이름이 없음
    Stone.EMPTY -> throw IllegalArgumentException("빈 칸은 돌 이름을 가질 수 없습니다.")
}
