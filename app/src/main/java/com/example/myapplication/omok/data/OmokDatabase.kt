package com.example.myapplication.omok.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 흩어진 Entity와 DAO를 하나로 묶는 조립도. Room은 여기 등록된 것만 처리한다.
// version: 테이블 모양을 바꿀 때마다 1씩 올려서 "구조가 달라졌다"고 알리는 번호.
@Database(
    entities = [UserEntity::class, GameRoomEntity::class, MoveEntity::class],
    version = 1,
)
abstract class OmokDatabase : RoomDatabase() {

    // 함수 몸통이 없는 이유는 DAO와 같다 — Room이 빌드 때 구현을 만든다.
    abstract fun userDao(): UserDao
    abstract fun gameRoomDao(): GameRoomDao
    abstract fun moveDao(): MoveDao

    companion object {
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
                ).build().also { database -> instance = database }
            }
    }
}
