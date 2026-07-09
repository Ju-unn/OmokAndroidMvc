package org.example.domain

class Stones private constructor(private val cells: MutableMap<Position, Stone>) {

    companion object {
        // 빈 돌 컬렉션 생성
        fun empty(): Stones = Stones(mutableMapOf())
    }

    // 해당 위치의 돌 반환, 없으면 EMPTY
    fun stoneAt(position: Position): Stone = cells[position] ?: Stone.EMPTY

    // 해당 위치에 돌 배치
    fun place(position: Position, stone: Stone) {
        cells[position] = stone
    }
}
