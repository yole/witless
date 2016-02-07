package ru.yole.witless

class Traverser(val panel: Panel, val start: Point, val mirrorStart: Point?) {
    var currentPath = Path(start)
    var mirrorPath = if (mirrorStart != null) Path(mirrorStart) else null
    var currentEnd = start
    var currentMirrorEnd = mirrorStart
    var visited = BooleanMap(panel.width + 1, panel.height + 1)
    var tryDirection: Direction? = Direction.Right

    init {
        visited[start] = true
    }

    fun buildNextPath(): Pair<Path, Path?>? {
        while (true) {
            if (advancePath(tryDirection)) {
                tryDirection = Direction.Right
            }
            else {
                if (currentPath.steps.isEmpty()) {
                    return null
                }
                tryDirection = retreatPath()
            }
            if (currentEnd in panel.targetLocations) {
                return currentPath to mirrorPath
            }
        }
    }

    fun advancePath(startDirection: Direction?): Boolean {
        var direction: Direction? = startDirection
        while (direction != null) {
            val nextPoint = currentEnd.step(direction)
            val nextMirrorPoint = currentMirrorEnd?.step(direction.mirror(true, true))
            if (canStepTo(nextPoint) && (nextMirrorPoint == null || canStepTo(nextMirrorPoint))) {
                currentPath = currentPath.append(direction)
                mirrorPath = mirrorPath?.append(direction.mirror(true, true))
                visited[nextPoint] = true
                currentEnd = nextPoint
                if (nextMirrorPoint != null) {
                    visited[nextMirrorPoint] = true
                    currentMirrorEnd = nextMirrorPoint
                }
                return true
            }
            direction = direction.next()
        }
        return false
    }

    fun retreatPath(): Direction? {
        visited[currentEnd] = false
        val lastStep = currentPath.steps.last()
        currentPath = currentPath.retreat()
        currentEnd = currentEnd.step(lastStep.back())

        if (mirrorPath != null) {
            visited[currentMirrorEnd!!] = false
            mirrorPath = mirrorPath!!.retreat()
            currentMirrorEnd = currentMirrorEnd!!.step(lastStep.mirror(true, true).back())
        }

        return lastStep.next()
    }

    fun canStepTo(point: Point) = point.x in 0..panel.width && point.y in 0..panel.height && !visited[point]
}
