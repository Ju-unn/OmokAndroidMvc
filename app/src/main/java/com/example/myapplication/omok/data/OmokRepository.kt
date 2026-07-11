package com.example.myapplication.omok.data

// Controller가 쓰는 저장/조회 창구. Controller는 "Room"이라는 단어를 몰라도 된다.
// 나중에 저장 방식을 서버로 바꿔도 이 클래스 뒤쪽만 갈아끼우면 된다.
class OmokRepository(
    private val userDao: UserDao,
    private val gameRoomDao: GameRoomDao,
    private val moveDao: MoveDao,
    private val gameResultDao: GameResultDao,
) {

    // --- 사용자 ---

    suspend fun addUser(name: String): Long = userDao.insert(UserEntity(name = name))

    suspend fun getUsers(): List<UserEntity> = userDao.getAll()

    suspend fun findUserByName(name: String): UserEntity? = userDao.findByName(name)

    // --- 게임방 ---

    suspend fun createRoom(title: String, blackUserId: Long, whiteUserId: Long): Long =
        gameRoomDao.insert(
            GameRoomEntity(title = title, blackUserId = blackUserId, whiteUserId = whiteUserId)
        )

    suspend fun getRooms(): List<GameRoomEntity> = gameRoomDao.getAll()

    suspend fun findRoom(roomId: Long): GameRoomEntity? = gameRoomDao.findById(roomId)

    // 승부 확정: 방 상태를 '종료'로 바꾸고,
    // ① 보관용 원본에 "누가 이겼다" 한 줄 추가 ② 조회용 카운터를 +1 갱신.
    // 원본이 남아 있으므로 이후 방을 다시 시작해도 전적은 사라지지 않는다.
    suspend fun finishRoom(roomId: Long, winnerStone: String) {
        gameRoomDao.updateResult(roomId, GameRoomEntity.STATUS_FINISHED, winnerStone)
        val room = gameRoomDao.findById(roomId) ?: return
        val winnerId = winnerUserId(room, winnerStone)
        val loserId = loserUserId(room, winnerStone)
        gameResultDao.insert(
            GameResultEntity(roomId = roomId, winnerUserId = winnerId, loserUserId = loserId)
        )
        userDao.incrementWins(winnerId)
        userDao.incrementLosses(loserId)
    }

    private fun winnerUserId(room: GameRoomEntity, winnerStone: String): Long {
        if (winnerStone == GameRoomEntity.STONE_BLACK) return room.blackUserId
        return room.whiteUserId
    }

    private fun loserUserId(room: GameRoomEntity, winnerStone: String): Long {
        if (winnerStone == GameRoomEntity.STONE_BLACK) return room.whiteUserId
        return room.blackUserId
    }

    // --- 수순 ---

    // 몇 번째 수인지는 호출하는 쪽이 계산하지 않는다 — 저장의 사정은 저장소가 안다.
    suspend fun saveMove(roomId: Long, position: String) {
        val nextNumber = moveDao.countMoves(roomId) + 1
        moveDao.insert(MoveEntity(roomId = roomId, moveNumber = nextNumber, position = position))
    }

    suspend fun getMovesInOrder(roomId: Long): List<MoveEntity> = moveDao.getMovesInOrder(roomId)

    // 다시 시작: 수순을 비우고 방을 다시 '진행중'으로 되돌린다
    suspend fun restartRoom(roomId: Long) {
        moveDao.deleteByRoom(roomId)
        gameRoomDao.updateResult(roomId, GameRoomEntity.STATUS_IN_PROGRESS, null)
    }

    // --- 전적 ---

    // 사용자마다 전적을 모은다. 승/패는 users의 카운터를 바로 읽고(조회용 캐시),
    // 진행중은 방 상태에서 센다. 카운터가 의심스러우면 game_results에서 재계산할 수 있다.
    suspend fun getUserRecords(): List<UserRecord> {
        val users = userDao.getAll()
        return users.map { user ->
            UserRecord(
                userName = user.name,
                wins = user.wins,
                losses = user.losses,
                inProgressCount = gameRoomDao.countInProgress(user.id),
            )
        }
    }
}
