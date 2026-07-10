package com.example.myapplication.omok.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// game_rooms 테이블: 저장 슬롯 = 게임방 하나.
// foreignKeys: blackUserId/whiteUserId는 users 테이블의 id를 가리킨다고 DB에 알려준다.
//   → 존재하지 않는 사용자 id로 방을 만들면 DB가 거부한다 (데이터 무결성).
// indices: 외래키 컬럼에는 색인을 만들어 "이 사용자의 방들" 검색을 빠르게 한다.
@Entity(
    tableName = "game_rooms",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["blackUserId"]),
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["whiteUserId"]),
    ],
    indices = [Index("blackUserId"), Index("whiteUserId")],
)
data class GameRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val blackUserId: Long,
    val whiteUserId: Long,
    // IN_PROGRESS(진행중) / FINISHED(종료)
    val status: String = STATUS_IN_PROGRESS,
    // 승리한 돌: "BLACK" / "WHITE", 진행중이면 null
    val winnerStone: String? = null,
    // 생성 시각 (1970년부터의 밀리초) — 방 목록을 최신순으로 정렬할 때 쓴다
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_FINISHED = "FINISHED"
        const val STONE_BLACK = "BLACK"
        const val STONE_WHITE = "WHITE"
    }
}
