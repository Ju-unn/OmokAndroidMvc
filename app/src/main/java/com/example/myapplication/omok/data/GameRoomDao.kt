package com.example.myapplication.omok.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameRoomDao {

    @Insert
    suspend fun insert(room: GameRoomEntity): Long

    // 최신 방이 목록 위에 오도록 생성 시각 내림차순(DESC)
    @Query("SELECT * FROM game_rooms ORDER BY createdAt DESC")
    suspend fun getAll(): List<GameRoomEntity>

    @Query("SELECT * FROM game_rooms WHERE id = :id")
    suspend fun findById(id: Long): GameRoomEntity?

    // 승부가 나면 상태와 승리한 돌을 한 번에 갱신
    @Query("UPDATE game_rooms SET status = :status, winnerStone = :winnerStone WHERE id = :id")
    suspend fun updateResult(id: Long, status: String, winnerStone: String?)
}
