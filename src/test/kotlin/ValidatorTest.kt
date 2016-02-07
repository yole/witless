package ru.yole.witless

import org.junit.Test
import org.junit.Assert.*

class ValidatorTest {
    @Test fun polyminoSimple() {
        val board = Board(3, 3)
        board.startLocations.add(Point(0, 3))
        board.targetLocations.add(Point(0, 0))

        board.set(0, 0, createTTetramino())

        val path = pathFromString("03RURULUL")
        val result = Validator(board, path)
        assertTrue(result.validate())
    }

    @Test fun polyminoRotatedPath() {
        val board = Board(4, 4)
        board.startLocations.add(Point(0, 4))
        board.targetLocations.add(Point(4, 0))
        board[0, 2] = CellObject.Polymino.rotated(createTTetraminoMap())

        val path = pathFromString("04URDRUUULDLUURRRDDDDRUUUU")
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
