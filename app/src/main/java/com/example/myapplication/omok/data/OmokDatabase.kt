package com.example.myapplication.omok.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// 흩어진 Entity와 DAO를 하나로 묶는 조립도. Room은 여기 등록된 것만 처리한다.
// version: 테이블 모양을 바꿀 때마다 1씩 올려서 "구조가 달라졌다"고 알리는 번호.
// v2: 결과 기록(game_results) 테이블 추가 + users에 전적 카운터(wins/losses) 추가.
//     game_results = 보관용 원본(누가 누구를 언제 이겼나), users의 카운터 = 조회용 캐시.
@Database(
    entities = [UserEntity::class, GameRoomEntity::class, MoveEntity::class, GameResultEntity::class],
    version = 2,
)
abstract class OmokDatabase : RoomDatabase() {

    // 함수 몸통이 없는 이유는 DAO와 같다 — Room이 빌드 때 구현을 만든다.
    abstract fun userDao(): UserDao
    abstract fun gameRoomDao(): GameRoomDao
    abstract fun moveDao(): MoveDao
    abstract fun gameResultDao(): GameResultDao

    companion object {
        // 마이그레이션: 이미 v1 모양의 DB 파일을 가진 폰을 v2 모양으로 바꾸는 절차.
        // 이게 없으면 기존 사용자의 데이터를 지우거나 앱이 크래시한다.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // ① 새 테이블 생성 (GameResultEntity 선언과 모양이 정확히 같아야 한다)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `game_results` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `roomId` INTEGER NOT NULL,
                        `winnerUserId` INTEGER NOT NULL,
                        `loserUserId` INTEGER NOT NULL,
                        `finishedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`roomId`) REFERENCES `game_rooms`(`id`)
                            ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`winnerUserId`) REFERENCES `users`(`id`)
                            ON UPDATE NO ACTION ON DELETE NO ACTION,
                        FOREIGN KEY(`loserUserId`) REFERENCES `users`(`id`)
                            ON UPDATE NO ACTION ON DELETE NO ACTION
                    )
                    """
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_game_results_roomId` ON `game_results` (`roomId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_game_results_winnerUserId` ON `game_results` (`winnerUserId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_game_results_loserUserId` ON `game_results` (`loserUserId`)")
                // ② 데이터 이사: 이미 끝난 방들의 승부를 결과 기록으로 옮긴다.
                //    (CASE WHEN = SQL의 if: 이긴 돌이 흑이면 흑 사용자가 승자)
                db.execSQL(
                    """
                    INSERT INTO game_results (roomId, winnerUserId, loserUserId, finishedAt)
                    SELECT id,
                           CASE WHEN winnerStone = 'BLACK' THEN blackUserId ELSE whiteUserId END,
                           CASE WHEN winnerStone = 'BLACK' THEN whiteUserId ELSE blackUserId END,
                           createdAt
                    FROM game_rooms
                    WHERE status = 'FINISHED' AND winnerStone IS NOT NULL
                    """
                )
                // ③ users에 전적 카운터 컬럼 추가
                db.execSQL("ALTER TABLE users ADD COLUMN wins INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE users ADD COLUMN losses INTEGER NOT NULL DEFAULT 0")
                // ④ 카운터 초기값을 원본 기록에서 계산해 채운다.
                //    기록 → 카운터는 언제든 이렇게 다시 만들 수 있다 (캐시 복구 절차와 동일)
                db.execSQL(
                    """
                    UPDATE users SET
                        wins = (SELECT COUNT(*) FROM game_results WHERE winnerUserId = users.id),
                        losses = (SELECT COUNT(*) FROM game_results WHERE loserUserId = users.id)
                    """
                )
            }
        }

        // DB 연결은 무거운 자원이라 앱 전체에 딱 하나만 만든다 (싱글턴).
        @Volatile
        private var instance: OmokDatabase? = null

        fun getInstance(context: Context): OmokDatabase =
            instance ?: synchronized(this) {
                // synchronized: 두 곳에서 동시에 호출해도 DB가 두 개 생기지 않게 하는 잠금
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    OmokDatabase::class.java,
                    "omok.db", // 폰 저장소에 생기는 실제 DB 파일 이름
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { database -> instance = database }
            }
    }
}
