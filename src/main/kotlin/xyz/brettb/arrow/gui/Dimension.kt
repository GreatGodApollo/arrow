package xyz.brettb.arrow.gui

import kotlin.math.absoluteValue
import kotlin.math.max

class Dimension(var x: Int, var y: Int) {

    init {
        x = x.absoluteValue
        y = y.absoluteValue
    }

    fun fitsInside(other: Dimension): Boolean {
        return x <= other.x && y <= other.y
    }

    operator fun plus(other: Dimension): Dimension {
        return of(x + other.x, y + other.y)
    }

    operator fun minus(other: Dimension): Dimension {
        return of(max(x - other.x, 0), max(y - other.y, 0))
    }

    override fun toString(): String {
        return "[$x, $y]"
    }

    companion object {
        fun of(x: Int, y: Int): Dimension {
            return Dimension(x, y)
        }

        fun square(xy: Int): Dimension {
            return of(xy, xy)
        }
    }

}