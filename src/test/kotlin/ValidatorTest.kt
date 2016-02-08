package ru.yole.witless

import org.junit.Test
import org.junit.Assert.*

class ValidatorTest {
    @Test fun polyminoSimple() {
        val board = Panel(3, 3)
        board.startLocations.add(Point(0, 3))
        board.targetLocations.add(Point(0, 0))

        board.set(0, 0, createTTetramino())

        val path = pathFromString("00RURULUL", 3)
        val result = Validator(board, path)
        assertTrue(result.validate())
    }

    @Test fun polyminoRotatedPath() {
        val board = Panel(4, 4)
        board.startLocations.add(Point(0, 4))
        board.targetLocations.add(Point(4, 0))
        board[0, 2] = CellObject.Polymino.rotated(createTTetraminoMap())

        val path = pathFromString("00URDRUUULDLUURRRDDDDRUUUU", 4)
        assertTrue(Validator(board, path).validate())
    }

    private fun createTTetramino(): CellObject.Polymino {
        val map = createTTetraminoMap()
        return CellObject.Polymino(map)
    }
}

fun createTTetraminoMap(): BooleanMap {
    val map = BooleanMap(2, 3)
    map[0, 0] = true
    map[0, 1] = true
    map[1, 1] = true
    map[0, 2] = true
    return map
}
