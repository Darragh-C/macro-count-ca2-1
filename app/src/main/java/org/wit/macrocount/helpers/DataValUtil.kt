package org.wit.macrocount.helpers
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