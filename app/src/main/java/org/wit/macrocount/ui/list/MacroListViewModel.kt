package org.wit.macrocount.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseMacroManager
//import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroListViewModel: ViewModel() {

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    private val macroList = MutableLiveData<List<MacroCountModel>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    init {
        load()
    }

    fun load() {
        try {
            Timber.i("Loading macros for ${FirebaseAuth.getInstance().currentUser!!.uid}")
            FirebaseMacroManager.findAll(FirebaseAuth.getInstance().currentUser!!.uid, macroList)
            Timber.i("Retrofit Success : $macroList.value")
        }
        catch (e: Exception) {
            Timber.i("Retrofit error: ${e.message}")
        }
    }

    fun delete(userid: String, id: String) {
        Timber.i("macroListViewModel.delete $id")
        try {
            FirebaseMacroManager.delete(userid,id)
            Timber.i("Report Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Report Delete Error : $e.message")
        }
    }

}