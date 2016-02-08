package ru.yole.witless

enum class Direction {
    Right, Up, Left, Down;

    override fun toString() = name.take(1)

    fun next() = when(this) {
        Right -> Up
        Up -> Left
        Left -> Down
        Down -> null
    }

    fun back() = when(this) {
        Right -> Left
        Up -> Down
        Left -> Right
        Down -> Up
    }

    fun mirror(horizontally: Boolean, vertically: Boolean) = when(this) {
        Right -> if (horizontally) Left else Right
        Left -> if (horizontally) Right else Left
        Up -> if (vertically) Down else Up
        Down -> if (vertically) Up else Down
    }
}

data class Path(val start: Point, val steps: List<Direction> = listOf()) {
    operator fun plus(direction: Direction) = Path(start, steps.plusElement(direction))
    fun retreat() = Path(start, steps.dropLast(1))

    override fun toString() = "$start -> ${stepsToString()}"

    private fun stepsToString() = buildString {
        var repeatCount = 0
        var currentDirection: Direction? = null

        fun appendDirection() {
            if (currentDirection == null) return
            if (repeatCount <= 2) {
                append(currentDirection.toString())
                if (repeatCount == 2) {
                    append(currentDirection.toString())
                }
            }
            else {
                append(repeatCount)
                append(currentDirection.toString())
            }
        }

        for (step in steps) {
            if (currentDirection == step) {
                repeatCount++
            }
            else {
                appendDirection()
                currentDirection = step
                repeatCount = 1
            }
        }
        appendDirection()
    }
}
