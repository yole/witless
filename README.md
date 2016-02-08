= The Witless

This is an automated puzzle solving tool for [The Witness](http://the-witness.net/).
Many puzzles in the game require solutions derived according to well-defined rules, and this program can solve some of such puzzles.
Other types of puzzles depend on clues that you find in the game and cannot be solved by a computer.

The advantage of using this tool compared to a walkthrough is that this tool is guaranteed to give you a solution to a single puzzle on which
you might be stuck (or show you that there is no solution, which means that you need to look for more clues that will help you understand the
task). You will not accidentally see any spoilers for other puzzles when you look for the single solution that you need.

(Also, encoding puzzles for the solver is intentionally quite cumbersome, so you'll often find that you can solve the puzzle in the game
faster than it would take you to encode for the solver. However, if you're truly stuck, the encoding effort will not matter.)

== Supported Puzzle Elements

 * Colored blobs
 * Colored stars
 * Combinations of little yellow squares
 * Rotated combinations of little yellow squares
 * Little hexes on the lines and intersections of lines
 * Mirrored lines
 * Panels with broken lines

== Currently Unsupported Puzzle Elements

 * Non-square panels
 * White things with three points (like an inverted "Y" letter)
 * Little unfilled blue squares
 * Any other weird things you may find in the game

== Running The Witless

 * Make sure you have the Java Development Kit installed. You can download it from the [Oracle site](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
 * Create a file named "puzzle.txt" in the directory where you've checked out The Witless, and put your puzzle there. See below for encoding details.
 * On Windows, open the Command Prompt, use the "cd" command to navigate to the directory where you've checked out The Witless, and enter "gradlew run"
 * On Mac and Linux, run "./gradlew run" from the checkout directory.
 * The puzzle solution will be printed to the standard output.

== Encoding the Puzzle

The first line specifies the panel configuration. Here's an example:

    4400,44

The first two digits are the width and the height of the board.
The next sequence of digits specifies the coordinates of each start location of the panel.
The Y coordinate is counted starting from the bottom, so most panels will have the start at (0,0) and end at (4,4).
After the start locations, put a comma and list the target locations in the same way.

If the panel uses mirrored lines, put an "M" after the first start location and put the coordinates of the mirror start, for example:

   4400M44,0440

If the panel contains hexes, put in an "X" sign and list the coordinates of each hex, followed by its position relative to the
corresponding intersection of lines ("I" if it's at the intersection itself, "B" if it's below or "R" if it's to the right). Example:

   4400,44X22R24B

If the panel has a hex at every intersection, simply put the "X" twice:

   4400,44XX

If the panel contains broken horizontal or vertical lines, put in a slash, list the coordinates of broken horizontal lines
(or, more specifically, the coordinates of the intersection on the left side of the broken line), then after another slash
list the coordinates of broken vertical lines (the intersection below the broken line - remember, Y=0 is the bottom corner of the panel).

  5500,55/34/32


