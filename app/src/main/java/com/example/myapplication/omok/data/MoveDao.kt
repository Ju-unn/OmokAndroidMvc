package com.example.myapplication.omok.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoveDao {

    @Insert
    suspend fun insert(move: MoveEntity)

    // 복원의 핵심: 반드시 둔 순서(moveNumber)대로 가져와야 보드가 똑같이 재현된다
    @Query("SELECT * FROM moves WHERE roomId = :roomId ORDER BY moveNumber")
    suspend fun getMovesInOrder(roomId: Long): List<MoveEntity>

    // 다음 수의 번호를 정할 때 사용 (지금까지 둔 수 + 1)
    @Query("SELECT COUNT(*) FROM moves WHERE roomId = :roomId")
    suspend fun countMoves(roomId: Long): Int
}
