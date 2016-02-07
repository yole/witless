package ru.yole.witless

import org.junit.Test
import org.junit.Assert.*

class BooleanMapTest {
    @Test fun overlay() {
        val map = BooleanMap(3, 3)
        map[0, 0] = true

        val overlay = BooleanMap(2, 2)
        for (x in 0..1)
            for (y in 0..1)
                overlay[x, y] = true

        val overlaid = map.overlay(overlay, 1, 1)!!
        assertTrue(overlaid[0, 0])
        assertTrue(overlaid[1, 1])
        assertTrue(overlaid[2, 2])
    }

    @Test fun markCount() {
        val map = BooleanMap(3, 3)
        map[0, 0] = true
        map[0, 0] = true
        map[1, 1] = true

        assertEquals(2, map.markCount)
    }

    @Test fun invert() {
        val map = BooleanMap(3, 3)
        map[0, 0] = true
        map[1, 1] = true
        val inverted = map.invert()
        assertFalse(inverted[0, 0])
        assertFalse(inverted[1, 1])
        assertTrue(inverted[1, 0])
        assertTrue(inverted[0, 1])
    }

    @Test fun overlayAfterInvert() {
        val map = BooleanMap(3, 3)
        map[0, 0] = true
        map[1, 1] = true
        assertNotNull(map.overlay(map.invert(), 0, 0))
    }

    @Test fun rotate() {
        val map = createTTetraminoMap()
        val rotated = map.rotate()
        assertEquals(3, rotated.width)
        assertEquals(2, rotated.height)
        assertTrue(rotated[0, 0])
        assertTrue(rotated[1, 0])
        assertTrue(rotated[2, 0])
        assertTrue(rotated[1, 1])
    }
}
