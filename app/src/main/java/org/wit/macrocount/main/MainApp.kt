package org.wit.macrocount.main
import android.app.Application
import org.wit.macrocount.models.DayStore
import org.wit.macrocount.models.MacroCountJSONStore
import org.wit.macrocount.models.MacroCountStore
import org.wit.macrocount.models.UserJSONStore
import org.wit.macrocount.models.UserMemStore
import org.wit.macrocount.models.UserStore
import org.wit.macrocount.models.DayJSONStore
import timber.log.Timber
import timber.log.Timber.Forest.i

class MainApp : Application() {

    lateinit var macroCounts: MacroCountStore
    lateinit var users: UserStore
    lateinit var days: DayStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        macroCounts = MacroCountJSONStore(applicationContext)
        users = UserJSONStore(applicationContext)
        days = DayJSONStore(applicationContext)
        i("MacroCount started")
    }
}