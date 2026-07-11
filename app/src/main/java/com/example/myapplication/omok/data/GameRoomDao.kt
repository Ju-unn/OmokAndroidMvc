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

    // 전적은 테이블에 저장하지 않고 매번 방 개수를 세서 구한다 (1단계 설계 결정).
    // 승리: 끝난 방 중에서 "흑으로 참가했고 흑이 이김" 또는 "백으로 참가했고 백이 이김"
    @Query(
        """
        SELECT COUNT(*) FROM game_rooms
        WHERE status = 'FINISHED'
          AND ((blackUserId = :userId AND winnerStone = 'BLACK')
            OR (whiteUserId = :userId AND winnerStone = 'WHITE'))
        """
    )
    suspend fun countWins(userId: Long): Int

    // 패배: 끝난 방 중에서 참가했지만 상대 돌이 이긴 방
    @Query(
        """
        SELECT COUNT(*) FROM game_rooms
        WHERE status = 'FINISHED'
          AND ((blackUserId = :userId AND winnerStone = 'WHITE')
            OR (whiteUserId = :userId AND winnerStone = 'BLACK'))
        """
    )
    suspend fun countLosses(userId: Long): Int

    // 진행중: 참가하고 있고 아직 끝나지 않은 방
    @Query(
        """
        SELECT COUNT(*) FROM game_rooms
        WHERE status = 'IN_PROGRESS'
          AND (blackUserId = :userId OR whiteUserId = :userId)
        """
    )
    suspend fun countInProgress(userId: Long): Int
}
