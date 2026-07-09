package com.example.myapplication.omok.domain

enum class Stone {
    BLACK,
    WHITE,
    EMPTY;

    // 빈 칸인지 확인
    fun isEmpty(): Boolean = this == EMPTY
}
