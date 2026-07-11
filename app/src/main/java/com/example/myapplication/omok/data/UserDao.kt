package com.example.myapplication.omok.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// users 테이블에 대한 질의 모음. 함수 선언만 하면 구현은 Room이 빌드 때 자동 생성한다.
@Dao
interface UserDao {

    // 반환값 Long = 방금 발급된 id
    @Insert
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users ORDER BY id")
    suspend fun getAll(): List<UserEntity>

    // :name 자리에 파라미터 값이 안전하게 바인딩된다 (문자열 이어붙이기 금지)
    @Query("SELECT * FROM users WHERE name = :name")
    suspend fun findByName(name: String): UserEntity?

    // 승부 확정 시 카운터 갱신. "wins + 1"을 DB 안에서 계산하므로
    // 읽어서 더하고 다시 쓰는 것보다 안전하다 (그 사이에 끼어들 틈이 없다)
    @Query("UPDATE users SET wins = wins + 1 WHERE id = :userId")
    suspend fun incrementWins(userId: Long)

    @Query("UPDATE users SET losses = losses + 1 WHERE id = :userId")
    suspend fun incrementLosses(userId: Long)
}
