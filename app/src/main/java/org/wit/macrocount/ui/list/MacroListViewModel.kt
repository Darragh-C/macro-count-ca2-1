package org.wit.macrocount.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseDayManager
import org.wit.macrocount.firebase.FirebaseMacroManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber
import java.time.LocalDate

class MacroListViewModel: ViewModel() {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    private var macroList = MutableLiveData<ArrayList<MacroCountModel>>()

    val observableMacroList: MutableLiveData<ArrayList<MacroCountModel>>
        get() = macroList

    init {
        load()
    }

    fun load() {
        try {
            val userid = FirebaseAuth.getInstance().currentUser!!.uid
            Timber.i("Loading macros for ${userid}")
            val today = FirebaseDayManager.findByUserDate(userid, LocalDate.now())
            val todayMacroIds = today.userMacroIds
            val todayMacros = ArrayList<MacroCountModel>()

            todayMacroIds.forEach { m ->
                val macro = MutableLiveData<MacroCountModel>()
                FirebaseMacroManager.findById(userid, m, macro)
                todayMacros.add(macro.value!!) // Access the value inside MutableLiveData
                Timber.i("Macro : $m")
            }

            macroList.value = todayMacros

            Timber.i("Retrofit Success : ${macroList.value}")
        } catch (e: Exception) {
            Timber.i("Retrofit error: ${e.message}")
        }
    }

    fun delete(userid: String, macroid: String) {
        Timber.i("delete macro from today $macroid ")
        try {
            FirebaseDayManager.removeMacro(userid, LocalDate.now().toString(), macroid)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }

}