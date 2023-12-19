package org.wit.macrocount.utils
object DataValUtil {

    fun validNum(string: String): Boolean {
        try {
            val number = string.toDouble()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }
}