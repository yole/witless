package ru.yole.witless

import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val lines = try {
        File("puzzle.txt").readLines()
    }
    catch(e: IOException) {
        println("Please put the puzzle to solve in puzzle.txt")
        exitProcess(1)
    }

    val board = boardFromString(lines[0])
    loadCells(board, lines.drop(1).joinToString(" "))
    var bestPath: Path? = findBestValidPath(board)

    if (bestPath != null) {
        println("Found solution: $bestPath")
    }
    else {
        println("No solution found")
    }
}

private fun findBestValidPath(panel: Panel): Path? {
    var bestPath: Path? = null
    for (location in panel.startLocations) {
        val traverser = Traverser(panel, location, panel.mirrorStartLocation)
        while (true) {
            val (path, mirrorPath) = traverser.buildNextPath() ?: break
            if (Validator(panel, path, mirrorPath).validate()) {
                if (bestPath == null || bestPath.steps.size > path.steps.size) {
                    bestPath = path
                }
            }
        }
    }
    return bestPath
}
