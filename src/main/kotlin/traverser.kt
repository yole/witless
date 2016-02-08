package ru.yole.witless

class Traverser(val panel: Panel, val start: Point, val mirrorStart: Point?) {
    var currentPathState = PathState(start)
    var mirrorPathState = if (mirrorStart != null) PathState(mirrorStart) else null
    var visited = BooleanMap(panel.width + 1, panel.height + 1)
    var tryDirection: Direction? = Direction.Right

    inner class PathState(start: Point) {
        var path = Path(start)
        var end = start

        fun onTarget() = end in panel.targetLocations

        fun canStep(direction: Direction): Boolean {
            return canStepTo(end.step(direction)) && panel.canMove(end, direction)
        }

        fun canStepTo(point: Point) = point.x in 0..panel.width && point.y in 0..panel.height && !visited[point]

        fun step(direction: Direction) {
            val nextPoint = end.step(direction)
            path += direction
            visited[nextPoint] = true
            end = nextPoint
        }

        fun retreat(): Direction {
            visited[end] = false
            val lastStep = path.steps.last()
            path = path.retreat()
            end = end.step(lastStep.back())
            return lastStep
        }
    }

    init {
        visited[start] = true
    }

    fun buildNextPath(): Pair<Path, Path?>? {
        while (true) {
            if (advancePath(tryDirection)) {
                tryDirection = Direction.Right
            }
            else {
                if (currentPathState.path.steps.isEmpty()) {
                    return null
                }
                tryDirection = retreatPath()
            }
            if (currentPathState.onTarget() && (mirrorPathState?.onTarget() ?: true)) {
                return currentPathState.path to mirrorPathState?.path
            }
        }
    }

    fun advancePath(startDirection: Direction?): Boolean {
        var direction: Direction? = startDirection
        while (direction != null) {
            val mirrorDirection = direction.mirror(true, true)
            if (currentPathState.canStep(direction) && mirrorPathState?.canStep(mirrorDirection) ?: true) {
                currentPathState.step(direction)
                mirrorPathState?.step(mirrorDirection)
                return true
            }
            direction = direction.next()
        }
        return false
    }

    fun retreatPath(): Direction? {
        val result = currentPathState.retreat()
        mirrorPathState?.retreat()
        return result.next()
    }
}
