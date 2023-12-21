package org.wit.macrocount.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseDayManager
import org.wit.macrocount.firebase.FirebaseMacroManager
import org.wit.macrocount.firebase.FirebaseProfileManager
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.ui.login.LoggedInViewModel
import timber.log.Timber
import java.time.LocalDate

class MacroListViewModel: ViewModel() {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    var favourites = MutableLiveData<List<String>>()

    private var macroList = MutableLiveData<ArrayList<MacroCountModel>>()

    val observableMacroList: MutableLiveData<ArrayList<MacroCountModel>>
        get() = macroList

    val observableFavourites: MutableLiveData<List<String>>
        get() = favourites

    var snapshotCheck = MutableLiveData<Boolean>()

    val observableSnapshotCheck: MutableLiveData<Boolean>
        get() = snapshotCheck

    init {
        Timber.i("init block called")
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            FirebaseDayManager.snapshotCheck(currentUser.uid) { result ->
                if (result) {
                    loadDayMacros(currentUser.uid)
                    getFavourites(currentUser, favourites)
                    Timber.i("snapshot true called")
                    snapshotCheck.value = true
                } else {
                    Timber.i("snapshot failed at init block")
                }
            }
        } else {
            Timber.i("User not authenticated")
        }
    }

    fun loadDayMacros(userid: String) {
        Timber.i("loadDayMacros block called")
        Timber.i("loadDayMacros snapshotCheck state called: ${observableSnapshotCheck.value}")
        Timber.i("loadDayMacros macroList state called: ${macroList.value}")

        Timber.i("Loading macros for $userid")
        FirebaseDayManager.findByUserDate(userid, LocalDate.now()) { dayResult ->
            Timber.i("loadDayMacros findByUserDate Result : $dayResult")
            val todayMacroIds = dayResult?.userMacroIds
            Timber.i("loadDayMacros findByUserDate Result macro ids: $todayMacroIds")

            if (todayMacroIds.isNullOrEmpty()) {
                Timber.i("No macros today")
                macroList.value = ArrayList<MacroCountModel>()
                Timber.i("No macros today list: ${macroList.value}")
            } else {
                val todayMacros = ArrayList<MacroCountModel>()
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
                            Timber.i("todayMacros: ${todayMacros}")
                            macroList.value = todayMacros
                            Timber.i("loadDayMacros Success : ${macroList.value}")
                        } else {
                            Timber.i("loadDayMacros failed to find macro by id: $m")
                        }
                    }
                }

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

    fun handleFavourite(macroCount: MacroCountModel, isFavourite: Boolean, firebaseUser: FirebaseUser) {
        //add to user's favs
        if (isFavourite) {
            Timber.i("Adding macro to favourites")
            FirebaseProfileManager.addFavourite(macroCount.uid!!, firebaseUser)
        } else {
            Timber.i("Removing macro from favourites")
            FirebaseProfileManager.removeFavourite(macroCount.uid!!, firebaseUser)
        }
    }

    fun getFavourites(firebaseUser: FirebaseUser, favourites: MutableLiveData<List<String>>) {
        Timber.i("Getting favourites list view model")
        FirebaseProfileManager.getFavourites(firebaseUser, favourites)
    }

}