package ru.yole.witless

class Validator(val board: Board, val path: Path, val mirrorPath: Path? = null) {
    inner class Region {
        val points = mutableListOf<Point>()
        lateinit var map: BooleanMap

        fun add(point: Point) {
            points.add(point)
        }

        fun buildMap() {
            val (minX, maxX) = size(Point::x)
            val (minY, maxY) = size(Point::y)
            map = BooleanMap(maxX - minX + 1, maxY - minY + 1)
            for (point in points) {
                map[point.x - minX, point.y - minY] = true
            }
        }

        fun size(coord: (Point) -> Int): Pair<Int, Int> {
            val coords = points.map(coord)
            return coords.min()!! to coords.max()!!
        }
    }

    val pathToRight = BooleanMap(board.width, board.height)
    val pathBelow = BooleanMap(board.width, board.height)
    val floodFilled = BooleanMap(board.width, board.height)

    fun validate(): Boolean {
        if (!tracePath()) return false
        while (true) {
            val region = buildNextRegion() ?: return true
            if (!region.validate()) return false
        }
    }

    private fun tracePath(): Boolean {
        var hexesTaken = traceOnePath(path)
        if (mirrorPath != null) {
            hexesTaken += traceOnePath(mirrorPath)
        }
        return hexesTaken == board.hexCount
    }

    private fun traceOnePath(path: Path): Int {
        var hexesTaken = 0
        var curPoint = path.start
        if (board.hasHexAtIntersection(curPoint)) hexesTaken++

        for (direction in path.steps) {
            when (direction) {
                Direction.Right -> if (curPoint.y > 0) pathBelow[curPoint.x, curPoint.y - 1] = true
                Direction.Left -> if (curPoint.y > 0) pathBelow[curPoint.x - 1, curPoint.y - 1] = true
                Direction.Down -> if (curPoint.x > 0) pathToRight[curPoint.x - 1, curPoint.y] = true
                Direction.Up -> if (curPoint.x > 0) pathToRight[curPoint.x - 1, curPoint.y - 1] = true
            }
            if (board.hasHexAtLine(curPoint, direction)) hexesTaken++
            curPoint = curPoint.step(direction)
            if (board.hasHexAtIntersection(curPoint)) hexesTaken++
        }
        if (curPoint !in board.targetLocations) {
            throw IllegalArgumentException("Path ends at $curPoint which is not a target location")
        }
        return hexesTaken
    }

    private fun buildNextRegion(): Region? {
        val pt = findUnfilledPoint() ?: return null
        val region = Region()
        region.floodFill(pt)
        return region
    }

    private fun findUnfilledPoint(): Point? {
        for (x in 0..board.width-1) {
            for (y in 0..board.height-1) {
                if (!floodFilled[x, y]) return Point(x, y)
            }
        }
        return null
    }

    private fun Region.floodFill(point: Point) {
        if (floodFilled[point]) return
        floodFilled[point] = true
        add(point)
        if (point.x < board.width-1 && !pathToRight[point]) floodFill(point.step(Direction.Right))
        if (point.x > 0 && !pathToRight[point.x-1, point.y]) floodFill(point.step(Direction.Left))
        if (point.y < board.height-1 && !pathBelow[point]) floodFill(point.step(Direction.Down))
        if (point.y > 0 && !pathBelow[point.x, point.y-1]) floodFill(point.step(Direction.Up))
    }

    private fun Region.validate(): Boolean {
        val polyminos = mutableListOf<CellObject.Polymino>()
        val colors = IntArray(8)
        val stars = BooleanArray(8)
        var blobColor: Color? = null
        for (point in points) {
            val obj = board[point.x, point.y] ?: continue
            obj.color?.let { colors[it.ordinal]++ }
            when (obj) {
                is CellObject.Star -> {
                    stars[obj.color.ordinal] = true
                }
                is CellObject.Blob -> {
                    if (blobColor == null) {
                        blobColor = obj.color
                    }
                    else if (blobColor != obj.color) {
                        return false
                    }
                }
                is CellObject.Polymino -> polyminos.add(obj)
            }
        }
        for (i in 0..7) {
            if (stars[i] && colors[i] != 2) return false
        }
        if (polyminos.isNotEmpty() && !validatePolyminos(polyminos)) return false
        return true
    }

    private fun Region.validatePolyminos(polyminos: Collection<CellObject.Polymino>): Boolean {
        val polyminoSize = polyminos.sumBy { it.rotations.first().markCount }
        if (polyminoSize != points.size) return false
        buildMap()
        return validatePolyminosRecursive(polyminos, map.invert())
    }

    private fun validatePolyminosRecursive(polyminos: Collection<CellObject.Polymino>, map: BooleanMap): Boolean {
        if (polyminos.isEmpty()) return true
        for (polyminoMap in polyminos.first().rotations) {
            for (xShift in 0..map.width - polyminoMap.width) {
                for (yShift in 0..map.height - polyminoMap.height) {
                    val overlaidMap = map.overlay(polyminoMap, xShift, yShift)
                    if (overlaidMap != null && validatePolyminosRecursive(polyminos.drop(1), overlaidMap)) return true
                }
            }
        }
        return false
    }
}
