package com.example.sketchcrew.utils
import java.util.UUID



data class FileNameGen(
    val prefix: String = "drawing_",
    val extension: String = ".png",
    val jpgExtension: String  = ".jpg"
) {
    fun generateFileNamePNG(): String {
        val uuid = UUID.randomUUID().toString()
        val timeStamp = System.currentTimeMillis()
        return "$prefix${timeStamp}_$uuid$extension"
    }

    fun generateFileNameJPEG(): String {
        val uuid = UUID.randomUUID().toString()
        val timeStamp = System.currentTimeMillis()
        return "$prefix${timeStamp}_$uuid$jpgExtension"
    }
}
