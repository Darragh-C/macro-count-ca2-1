package org.wit.macrocount.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.firebase.FirebaseDBManager
//import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroDetailViewModel : ViewModel() {
    private val macro = MutableLiveData<MacroCountModel>()

    val observableMacro: LiveData<MacroCountModel>
        get() = macro

    fun getMacro(userid:String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, macro)
            Timber.i("Detail getMacro() Success : ${
                macro.value}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : $e.message")
        }
    }
}