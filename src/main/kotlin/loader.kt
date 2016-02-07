package ru.yole.witless

class StringReader(val data: String) {
    private var position = 0

    fun isEOF() = position == data.length
    fun read() = data[position++]
    fun peek() = data[position]

    fun check(c: Char): Boolean {
        if (peek() == c) {
            position++
            return true
        }
        return false
    }

    fun expect(c: Char) {
        if (data[position++] != c) throw IllegalArgumentException("$c expected")
    }
}

fun StringReader.readDigit(): Int {
    val c = read()
    if (c in '0'..'9') return c - '0'
    throw IllegalArgumentException("Not a digit: $c")
}

fun StringReader.readPoint() = Point(readDigit(), readDigit())

fun boardFromString(encodedBoard: String): Board {
    val reader = StringReader(encodedBoard)
    val width = reader.readDigit()
    val height = reader.readDigit()
    val result = Board(width, height)

    result.startLocations.add(reader.readPoint())
    if (reader.check('M')) {
        result.mirrorStartLocation = reader.readPoint()
    }
    reader.expect(',')
    while (!reader.isEOF() && reader.peek() != 'X') {
        result.targetLocations.add(reader.readPoint())
    }

    if (reader.check('X')) {
        reader.loadHexes(result)
    }

    return result
}

fun StringReader.loadHexes(board: Board) {
    if (check('X')) {
        board.fillIntersectionsWithHexes()
        return
    }
    while (!isEOF()) {
        val point = readPoint()
        val location = readHexLocation()
        board.putHex(point.x, point.y, location)
    }
}

fun StringReader.readHexLocation(): HexLocation {
    val c = read().toUpperCase()
    return HexLocation.values().find { it.name.startsWith(c) } ?: throw IllegalArgumentException("Unknown hex location $c")
}

fun StringReader.readColor(): Color {
    val c = read()
    return when (c) {
        'K' -> Color.Black
        else -> Color.values().find { it.name.startsWith(c) } ?: throw IllegalArgumentException("Unknown color $c")
    }
}

fun StringReader.readPolymino(rotated: Boolean): CellObject.Polymino {
    expect('(')
    val rows = arrayListOf(StringBuilder())
    while(peek() != ')') {
        val c = read()
        if (c == '/')
            rows.add(StringBuilder())
        else
            rows.last().append(c)
    }
    expect(')')

    val width = rows.first().length
    if (!rows.all { it.length == width })
        throw IllegalArgumentException("Polymino rows need to have equal width")

    val map = BooleanMap(width, rows.size)
    for ((y, row) in rows.withIndex()) {
        for ((x, cell) in row.withIndex()) {
            if (cell != '_') map[x, y] = true
        }
    }
    return if (rotated) CellObject.Polymino.rotated(map) else CellObject.Polymino(map)
}

fun StringReader.readCell(): CellObject? {
    while (true) {
        if (isEOF()) throw IllegalArgumentException("Out of encoded characters")
        val c = read()
        if (Character.isWhitespace(c)) {
            continue
        }
        return when(c) {
            '_' -> null
            'O' -> CellObject.Blob(readColor())
            '*' -> CellObject.Star(readColor())
            'T' -> readPolymino(false)
            't' -> readPolymino(true)
            else -> throw IllegalArgumentException("Unknown object type $c")
        }
    }
}

fun loadCells(board: Board, encodedCells: String) {
    val reader = StringReader(encodedCells)
    for (y in 0..board.height-1) {
        for (x in 0..board.width-1) {
            try {
                board[x, y] = reader.readCell()
            } catch(e: IllegalArgumentException) {
                throw IllegalArgumentException("Error reading at ($x,$y): ${e.message}")
            }
       }
    }
}

fun Char.toDirection() = when(this) {
    'R' -> Direction.Right
    'U' -> Direction.Up
    'L' -> Direction.Left
    'D' -> Direction.Down
    else -> throw IllegalArgumentException("Unknown direction $this")
}

fun pathFromString(encodedPath: String): Path {
    val reader = StringReader(encodedPath)
    val start = reader.readPoint()
    var result = Path(start)
    while (!reader.isEOF()) {
        result = result.append(reader.read().toDirection())
    }
    return result
}
