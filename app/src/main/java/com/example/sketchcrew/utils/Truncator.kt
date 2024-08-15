package com.example.sketchcrew.utils

class Truncator(text: String, lengthText: Int, ellipsis: Boolean = true) {
    var txt = text
    var len = lengthText
    var ellipsis = ellipsis

    var truncatedStr = ""
    fun textTruncate(): String {
        truncatedStr = if (txt.length <= len) {
            txt
        } else {
            if (ellipsis && len > 3) {
                txt.substring(0, len - 3) + "..."
            } else {
                txt.substring(0, len)
            }
        }
        return truncatedStr
    }
}