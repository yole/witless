package ru.yole.witless

enum class Color { White, Black, Red, Green, Blue, Yellow, Magenta, Cyan }

sealed class CellObject() {
    open val color: Color? get() = null

    class Star(override val color: Color) : CellObject()
    class Blob(override val color: Color) : CellObject()

    class Polymino(val rotations: Collection<BooleanMap>) : CellObject() {
        constructor(map: BooleanMap) : this(listOf(map))

        companion object {
            fun rotated(map: BooleanMap): Polymino {
                val rotations = generateSequence(map) { it.rotate() }
                return Polymino(rotations.take(4).toList())
            }
        }
    }
}

enum class HexLocation { Intersection, Right, Below }

class Board(val width: Int, val height: Int) {
    val startLocations = mutableListOf<Point>()
    val targetLocations = mutableListOf<Point>()
    var mirrorStartLocation: Point? = null

    val cells = Array(width) { arrayOfNulls<CellObject>(height) }
    val hexesAtIntersections = BooleanMap(width + 1, height + 1)
    val hexesRight = BooleanMap(width + 1, height + 1)
    val hexesBelow = BooleanMap(width + 1, height + 1)

    val hexCount: Int
        get() = hexesAtIntersections.markCount + hexesRight.markCount + hexesBelow.markCount

    operator fun get(point: Point) = cells[point.x][point.y]
    operator fun get(x: Int, y: Int) = cells[x][y]

    operator fun set(x: Int, y: Int, obj: CellObject?) {
        cells[x][y] = obj
    }

    fun putHex(x: Int, y: Int, location: HexLocation) {
        val map = when (location) {
            HexLocation.Intersection -> hexesAtIntersections
            HexLocation.Right -> hexesRight
            HexLocation.Below -> hexesBelow
        }
        map[x, y] = true
    }

    fun fillIntersectionsWithHexes() {
        for (x in 0..width) {
            for (y in 0..height) {
                putHex(x, y, HexLocation.Intersection)
            }
        }
    }

    fun hasHexAtIntersection(point: Point) = hexesAtIntersections[point]

    fun hasHexAtLine(point: Point, direction: Direction) = when (direction) {
        Direction.Right -> hexesRight[point]
        Direction.Left -> hexesRight[point.x-1, point.y]
        Direction.Down -> hexesBelow[point]
        Direction.Up -> hexesBelow[point.x, point.y-1]
    }
}
