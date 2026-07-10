package com.example.myapplication.omok.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// moves 테이블: 돌을 놓은 순서 기록. 보드(15×15) 전체가 아니라 수순만 저장한다.
// onDelete = CASCADE: 방이 삭제되면 그 방의 수순도 DB가 알아서 함께 지운다.
@Entity(
    tableName = "moves",
    foreignKeys = [
        ForeignKey(
            entity = GameRoomEntity::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("roomId")],
)
data class MoveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    // 몇 번째 수인지 (1부터). 복원할 때 이 순서대로 다시 둔다.
    val moveNumber: Int,
    // "H8" 같은 좌표 문자열
    val position: String,
)
