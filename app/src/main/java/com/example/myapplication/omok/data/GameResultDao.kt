package com.example.myapplication.omok.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameResultDao {

    @Insert
    suspend fun insert(result: GameResultEntity)

    // 승수 = 이 사용자가 승자로 기록된 결과의 개수 (여러 방, 여러 판 전부 합산)
    @Query("SELECT COUNT(*) FROM game_results WHERE winnerUserId = :userId")
    suspend fun countWins(userId: Long): Int

    @Query("SELECT COUNT(*) FROM game_results WHERE loserUserId = :userId")
    suspend fun countLosses(userId: Long): Int
}
