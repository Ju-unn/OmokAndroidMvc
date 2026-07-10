package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.omok.controller.GameController
import com.example.myapplication.omok.data.OmokDatabase
import com.example.myapplication.omok.data.OmokRepository
import com.example.myapplication.omok.view.OmokScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// 앱 진입점: Controller를 생성하고 View(OmokScreen)와 연결 (콘솔 Main.kt의 역할)
class MainActivity : ComponentActivity() {

    private lateinit var controller: GameController

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
        controller = GameController(repository, lifecycleScope)

        // 임시 연결 — 8단계에서 사용자 관리·방 목록 화면이 생기면 교체된다.
        // 지금은 기본 사용자 2명과 연습 방 하나를 만들어 저장이 동작하는지 확인한다.
        lifecycleScope.launch {
            val roomId = prepareDefaultRoom(repository)
            controller.attachRoom(roomId)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OmokScreen(
                        controller = controller,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // 기본 사용자(흑돌/백돌)를 한 번만 만들고, 매 실행마다 새 연습 방을 연다
    // (6단계에서 "최근 진행중 방 이어하기"로 바뀐다)
    private suspend fun prepareDefaultRoom(repository: OmokRepository): Long {
        val blackId = repository.findUserByName("흑돌")?.id ?: repository.addUser("흑돌")
        val whiteId = repository.findUserByName("백돌")?.id ?: repository.addUser("백돌")
        return repository.createRoom("연습 게임", blackId, whiteId)
    }
}
