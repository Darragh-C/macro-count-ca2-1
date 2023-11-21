package org.wit.macrocount.models

import android.content.Context
import android.content.SharedPreferences

class UserRepo(context: Context) {

    private val prefs = context.getSharedPreferences("org.wit.macrocount.main.MainApp", Context.MODE_PRIVATE)

    var userId: String?
        get() = prefs.getString("userId", null)

        set(value) {
            prefs.edit().putString("userId", value).apply()
        }
}