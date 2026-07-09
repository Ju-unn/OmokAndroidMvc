package org.example.view

import java.io.BufferedReader
import java.io.PrintStream

class InputView(
    private val reader: BufferedReader,
    private val output: PrintStream
) {

    companion object {
        // 표준 입출력으로 InputView 생성 (UTF-8 인코딩)
        fun create(): InputView {
            val reader = System.`in`.bufferedReader(Charsets.UTF_8)
            val output = PrintStream(System.out, true, "UTF-8")
            return InputView(reader, output)
        }
    }

    // 프롬프트 출력 후 입력값을 읽어 공백 제거 및 대문자 변환하여 반환
    fun readInput(): String {
        prompt("위치를 입력하세요: ")
        val input = reader.readLine() ?: ""
        val trimmed = input.trim()
        return trimmed.uppercase()
    }

    // 줄바꿈 없이 메시지 출력 (입력 프롬프트용)
    private fun prompt(message: String) = output.print(message)
}
