package ru.yole.witless

data class Point(val x: Int, val y: Int) {
    fun step(direction: Direction) = when (direction) {
        Direction.Right -> Point(x + 1, y)
        Direction.Up -> Point(x, y - 1)
        Direction.Left -> Point(x - 1, y)
        Direction.Down -> Point(x, y + 1)
    }
}

class BooleanMap(val width: Int, val height: Int) {
    private val data = IntArray(height)
    var markCount: Int = 0
        private set

    operator fun get(point: Point): Boolean = data[point.y] and (1 shl point.x) != 0
    operator fun get(x: Int, y: Int): Boolean = data[y] and (1 shl x) != 0

    operator fun set(point: Point, value: Boolean) {
        set(point.x, point.y, value)
    }

    operator fun set(x: Int, y: Int, value: Boolean) {
        if (x < 0 || x >= width) throw IndexOutOfBoundsException("Index $x out of bounds (0..${width-1})")
        val oldValue = get(x, y)
        if (value)
            data[y] = data[y] or (1 shl x)
        else
            data[y] = data[y] and (1 shl x).inv()

        if (!oldValue && value) {
            markCount++
        }
        else if (oldValue && !value) {
            markCount--
        }
    }

    fun overlay(other: BooleanMap, xShift: Int, yShift: Int): BooleanMap? {
        val copy = BooleanMap(width, height)
        for (y in 0..height-1) {
            val thisRow = data[y]
            val otherY = y - yShift
            if (otherY < 0 || otherY >= other.height)
                copy.data[y] = thisRow
            else
            {
                val otherRow = other.data[otherY] shl xShift
                if (thisRow and otherRow != 0) return null
                copy.data[y] = thisRow or otherRow
            }
        }
        return copy
    }

    fun invert(): BooleanMap {
        val result = BooleanMap(width, height)
        for (y in 0..height - 1) {
            result.data[y] = data[y].inv() and ((1 shl width) - 1)
        }
        result.markCount = width * height - markCount
        return result
    }

    fun rotate(): BooleanMap {
        val result = BooleanMap(height, width)
        for (x in 0..width-1) {
            for (y in 0..height-1) {
                result[height - y - 1, x] = this[x, y]
            }
        }
        return result
    }

    override fun toString(): String = buildString {
        for (y in 0..height - 1) {
            for (x in 0..width - 1) {
                append(if (get(x, y)) '*' else ' ')
            }
            append('\n')
        }
    }
}
