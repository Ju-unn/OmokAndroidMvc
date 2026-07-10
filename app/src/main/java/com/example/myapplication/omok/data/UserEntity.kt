package com.example.myapplication.omok.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// users 테이블 한 줄의 모양. 로그인 없는 로컬 프로필이라 id와 이름만 가진다.
@Entity(tableName = "users")
data class UserEntity(
    // autoGenerate = true: id를 직접 정하지 않고 DB가 1, 2, 3… 순서로 발급한다.
    // 새로 만들 때는 0을 넣으면 "아직 id 없음"으로 취급되어 자동 발급된다.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)
