package com.example.myapplication.omok.data

// 사용자 한 명의 전적. DB 테이블이 아니라 COUNT 질의 결과를 모은 계산값이다.
data class UserRecord(
    val userName: String,
    val wins: Int,
    val losses: Int,
    val inProgressCount: Int,
)
