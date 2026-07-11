package com.example.myapplication.omok.data

// Controller가 쓰는 저장/조회 창구. Controller는 "Room"이라는 단어를 몰라도 된다.
// 나중에 저장 방식을 서버로 바꿔도 이 클래스 뒤쪽만 갈아끼우면 된다.
class OmokRepository(
    private val userDao: UserDao,
    private val gameRoomDao: GameRoomDao,
    private val moveDao: MoveDao,
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

    suspend fun finishRoom(roomId: Long, winnerStone: String) =
        gameRoomDao.updateResult(roomId, GameRoomEntity.STATUS_FINISHED, winnerStone)

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

    // 사용자마다 승/패/진행중 개수를 세서 모은다. 저장된 값이 아니라 매번 계산한 값이다.
    suspend fun getUserRecords(): List<UserRecord> {
        val users = userDao.getAll()
        return users.map { user ->
            UserRecord(
                userName = user.name,
                wins = gameRoomDao.countWins(user.id),
                losses = gameRoomDao.countLosses(user.id),
                inProgressCount = gameRoomDao.countInProgress(user.id),
            )
        }
    }
}
