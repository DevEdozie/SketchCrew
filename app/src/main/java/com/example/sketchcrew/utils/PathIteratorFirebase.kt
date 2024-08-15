package com.example.sketchcrew.utils

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF


class PathIteratorFirebase(path: Path) : Iterable<PointF> {
    private val pathMeasure = PathMeasure(path, false)
    private val pathLength = pathMeasure.length
    private var currentDistance = 0f
    private val interval = 5f // Change this to get more or fewer points

    override fun iterator(): Iterator<PointF> {
        return object : Iterator<PointF> {
            override fun hasNext(): Boolean {
                return currentDistance < pathLength
            }

            override fun next(): PointF {
                val position = FloatArray(2)
                pathMeasure.getPosTan(currentDistance, position, null)
                currentDistance += interval
                return PointF(position[0], position[1])
            }
        }
    }
}
