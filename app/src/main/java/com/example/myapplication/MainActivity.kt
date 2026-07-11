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
import com.example.myapplication.omok.controller.RoomListController
import com.example.myapplication.omok.controller.UserController
import com.example.myapplication.omok.data.OmokDatabase
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.view.OmokScreen
import com.example.myapplication.omok.view.RoomListScreen
import com.example.myapplication.omok.view.UserScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

// 화면 종류: 내비게이션 라이브러리 없이 "현재 화면" 상태값 하나로 전환한다
private enum class Screen {
    USER_MANAGEMENT,
    ROOM_LIST,
    GAME
}

// 앱 진입점: Controller들을 생성하고 View와 연결 (콘솔 Main.kt의 역할)
class MainActivity : ComponentActivity() {

    private lateinit var gameController: GameController
    private lateinit var userController: UserController
    private lateinit var roomListController: RoomListController

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
        roomListController = RoomListController(repository, lifecycleScope)
        userController.loadUsers()

        setContent {
            MyApplicationTheme {
                // 현재 화면 상태값. 이 값이 바뀌면 Compose가 다른 화면을 그린다
                var currentScreen by remember { mutableStateOf(Screen.USER_MANAGEMENT) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.USER_MANAGEMENT -> UserScreen(
                            controller = userController,
                            onRoomListClick = {
                                roomListController.loadRooms()
                                currentScreen = Screen.ROOM_LIST
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                        Screen.ROOM_LIST -> RoomListScreen(
                            controller = roomListController,
                            // 화면 사이의 값 전달: 입장할 방의 id가 콜백 인자로 올라온다
                            onRoomEnterClick = { roomId ->
                                gameController.attachRoom(roomId)
                                currentScreen = Screen.GAME
                            },
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
}
