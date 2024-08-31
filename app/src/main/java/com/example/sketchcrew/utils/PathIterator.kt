package com.example.sketchcrew.utils
import android.graphics.Path

class PathIterator(private val path: Path) {

    private val pathMeasure = android.graphics.PathMeasure(path, false)
    private var done = false
    private var position = FloatArray(6)
    private var tan = FloatArray(6)
    private var type = SEG_MOVETO

    companion object {
        const val SEG_MOVETO = 0
        const val SEG_LINETO = 1
        const val SEG_QUADTO = 2
        const val SEG_CUBICTO = 3
        const val SEG_CLOSE = 4
        const val SEG_ARCTO = 5
    }

    fun isDone(): Boolean = done

    fun next() {
        if (pathMeasure.nextContour()) {
            done = false
        } else {
            done = true
        }
    }

    fun currentSegment(coords: FloatArray): Int {
        if (done) return SEG_CLOSE

        val length = pathMeasure.length
        pathMeasure.getPosTan(length, position, tan)

        // Here you can decide how to segment the path based on your application.
        // This is just a very basic example, as actual segmentation needs more detailed handling.

        coords[0] = position[0]
        coords[1] = position[1]
        coords[2] = position[2]
        coords[3] = position[3]
        coords[4] = position[4]
        coords[5] = position[5]

        return type
    }
}
