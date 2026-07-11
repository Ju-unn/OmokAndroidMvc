package com.example.myapplication.omok.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// users 테이블 한 줄의 모양. 로컬 프로필 + 조회용 전적 카운터.
// wins/losses는 game_results(원본 기록)에서 언제든 다시 계산할 수 있는 캐시다.
// 승부가 날 때마다 +1 하고, 어긋나면 원본에서 재계산해 복구한다.
@Entity(tableName = "users")
data class UserEntity(
    // autoGenerate = true: id를 직접 정하지 않고 DB가 1, 2, 3… 순서로 발급한다.
    // 새로 만들 때는 0을 넣으면 "아직 id 없음"으로 취급되어 자동 발급된다.
    // 이름은 표시용일 뿐, 모든 관계는 이 id(PK)로 맺는다 — 동명이인 안전.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(defaultValue = "0")
    val wins: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val losses: Int = 0,
)
