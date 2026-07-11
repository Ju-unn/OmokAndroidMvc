package com.example.myapplication.omok.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// game_results 테이블: "누가 누구를 이겼다"는 확정된 사실 기록.
// 승부가 날 때마다 한 줄씩 추가만 하고, 수정·삭제하지 않는다 (역사는 쌓이기만 한다).
// 같은 방에서 다시 시작해 여러 판을 둬도 결과는 판마다 남으므로 전적이 보존된다.
@Entity(
    tableName = "game_results",
    foreignKeys = [
        ForeignKey(
            entity = GameRoomEntity::class,
            parentColumns = ["id"],
            childColumns = ["roomId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["winnerUserId"]),
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["loserUserId"]),
    ],
    indices = [Index("roomId"), Index("winnerUserId"), Index("loserUserId")],
)
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    // 이름이 아니라 users의 id를 가리킨다 — 동명이인이 있어도 안전
    val winnerUserId: Long,
    val loserUserId: Long,
    val finishedAt: Long = System.currentTimeMillis(),
)
