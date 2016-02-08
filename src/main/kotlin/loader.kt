package ru.yole.witless

class StringReader(val data: String) {
    private var position = 0

    fun isEOF() = position == data.length
    fun read() = data[position++]
    fun peek() = data[position]

    fun check(c: Char): Boolean {
        if (!isEOF() && peek() == c) {
            position++
            return true
        }
        return false
    }

    fun peekNoneOf(vararg c: Char) = !isEOF() && c.none { data[position] == it }

    fun expect(c: Char) {
        if (data[position++] != c) throw IllegalArgumentException("$c expected")
    }
}

fun StringReader.readDigit(): Int {
    val c = read()
    if (c in '0'..'9') return c - '0'
    throw IllegalArgumentException("Not a digit: $c")
}

fun StringReader.readPoint(height: Int) = Point(readDigit(), height - readDigit())

fun boardFromString(encodedBoard: String): Panel {
    val reader = StringReader(encodedBoard)
    val width = reader.readDigit()
    val height = reader.readDigit()
    val result = Panel(width, height)

    while (reader.peekNoneOf(',', 'M')) {
        result.startLocations.add(reader.readPoint(height))
    }
    if (reader.check('M')) {
        result.mirrorStartLocation = reader.readPoint(height)
    }
    reader.expect(',')
    while (reader.peekNoneOf('X', '/')) {
        result.targetLocations.add(reader.readPoint(height))
    }

    if (reader.check('X')) {
        reader.loadHexes(result)
    }
    if (reader.check('/')) {
        reader.loadBrokenLines(result)
    }

    return result
}

fun StringReader.loadHexes(panel: Panel) {
    if (check('X')) {
        panel.fillIntersectionsWithHexes()
        return
    }
    while (peekNoneOf('/')) {
        val point = readPoint(panel.height)
        val location = readHexLocation()
        panel.putHex(point.x, point.y, location)
    }
}

fun StringReader.loadBrokenLines(panel: Panel) {
    while (peekNoneOf('/')) {
        panel.addBrokenLineRight(readPoint(panel.width))
    }
    if (check('/')) {
        while (!isEOF()) {
            panel.addBrokenLineBelow(readPoint(panel.width))
        }
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

fun loadCells(panel: Panel, encodedCells: String) {
    val reader = StringReader(encodedCells)
    for (y in 0..panel.height-1) {
        for (x in 0..panel.width-1) {
            try {
                panel[x, y] = reader.readCell()
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

fun pathFromString(encodedPath: String, height: Int): Path {
    val reader = StringReader(encodedPath)
    val start = reader.readPoint(height)
    var result = Path(start)
    while (!reader.isEOF()) {
        result = result.plus(reader.read().toDirection())
    }
    return result
}
