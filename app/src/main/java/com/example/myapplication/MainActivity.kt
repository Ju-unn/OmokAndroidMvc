package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.omok.controller.GameController
import com.example.myapplication.omok.controller.UserController
import com.example.myapplication.omok.data.GameRoomEntity
import com.example.myapplication.omok.data.OmokDatabase
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.view.OmokScreen
import com.example.myapplication.omok.view.UserScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// 화면 종류: 내비게이션 라이브러리 없이 "현재 화면" 상태값 하나로 전환한다
private enum class Screen {
    USER_MANAGEMENT,
    GAME
}

// 앱 진입점: Controller들을 생성하고 View와 연결 (콘솔 Main.kt의 역할)
class MainActivity : ComponentActivity() {

    private lateinit var gameController: GameController
    private lateinit var userController: UserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = OmokDatabase.getInstance(this)
        val repository = OmokRepository(
            userDao = database.userDao(),
            gameRoomDao = database.gameRoomDao(),
            moveDao = database.moveDao(),
        )
        // lifecycleScope: 이 화면이 살아 있는 동안만 유효한 코루틴 작업장
        gameController = GameController(repository, lifecycleScope)
        userController = UserController(repository, lifecycleScope)
        userController.loadUsers()

        // 임시 연결 — 8단계에서 방 목록 화면이 생기면 방 선택 시 attachRoom 하도록 교체된다.
        lifecycleScope.launch {
            val roomId = prepareDefaultRoom(repository)
            gameController.attachRoom(roomId)
        }

        setContent {
            MyApplicationTheme {
                // 현재 화면 상태값. 이 값이 바뀌면 Compose가 다른 화면을 그린다
                var currentScreen by remember { mutableStateOf(Screen.USER_MANAGEMENT) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.USER_MANAGEMENT -> UserScreen(
                            controller = userController,
                            onGameStartClick = { currentScreen = Screen.GAME },
                            modifier = Modifier.padding(innerPadding)
                        )

                        Screen.GAME -> OmokScreen(
                            controller = gameController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    // 기본 사용자(흑돌/백돌)를 한 번만 만들고,
    // 진행 중인 방이 있으면 그 방을 이어서 열고, 없으면 새 연습 방을 만든다
    private suspend fun prepareDefaultRoom(repository: OmokRepository): Long {
        val blackId = repository.findUserByName("흑돌")?.id ?: repository.addUser("흑돌")
        val whiteId = repository.findUserByName("백돌")?.id ?: repository.addUser("백돌")
        val inProgress = repository.getRooms()
            .firstOrNull { room -> room.status == GameRoomEntity.STATUS_IN_PROGRESS }
        return inProgress?.id ?: repository.createRoom("연습 게임", blackId, whiteId)
    }
}
