package org.wit.macrocount.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseDayManager
import org.wit.macrocount.firebase.FirebaseMacroManager
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber
import java.time.LocalDate

class MacroListViewModel: ViewModel() {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    private var macroList = MutableLiveData<ArrayList<MacroCountModel>>()

    val observableMacroList: MutableLiveData<ArrayList<MacroCountModel>>
        get() = macroList

    var snapshotCheck = false

    init {
        Timber.i("init block executed")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            FirebaseDayManager.snapshotCheck(currentUser.uid) { result ->
                if (result) {
                    loadDayMacros(currentUser.uid)
                    snapshotCheck = true
                } else {
                    Timber.i("snapshot failed at init block")
                }
            }
        } else {
            Timber.i("User not authenticated")
        }
    }

    fun loadDayMacros(userid: String) {
        Timber.i("loadDayMacros block executed")

        Timber.i("Loading macros for $userid")
        FirebaseDayManager.findByUserDate(userid, LocalDate.now()) { dayResult ->
            Timber.i("loadDayMacros findByUserDate Result : $dayResult")
            val todayMacroIds = dayResult?.userMacroIds
            Timber.i("loadDayMacros findByUserDate Result macro ids: $todayMacroIds")
            val todayMacros = ArrayList<MacroCountModel>()

            if (todayMacroIds.isNullOrEmpty()) {
                Timber.i("No macros today")
                macroList.value = todayMacros
                Timber.i("No macros today list: ${macroList.value}")
            } else {
                Timber.i("Macro ids found for today: $todayMacroIds")
                todayMacroIds.forEach { m ->
                    val macro = MutableLiveData<MacroCountModel>()
                    Timber.i("loadDayMacros finding macro by id: $m")
                    FirebaseMacroManager.asyncFindById(userid, m, macro) {
                        result ->
                        if (result) {
                            Timber.i("loadDayMacros adding found macro to todayMacros: macro: ${macro}, macro.value: ${macro.value}")
                            macro.value?.let {
                                Timber.i("what is it: $it")
                                todayMacros.add(it)
                            }
                        } else {
                            Timber.i("loadDayMacros failed to find macro by id: $m")
                        }
                    }
                }
                Timber.i("todayMacros: $todayMacros")
                macroList.value = todayMacros
                Timber.i("loadDayMacros Success : ${macroList.value}")
            }
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