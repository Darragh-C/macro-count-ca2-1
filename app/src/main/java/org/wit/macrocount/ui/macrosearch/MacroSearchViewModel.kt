package org.wit.macrocount.ui.macrosearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import org.wit.macrocount.firebase.FirebaseDBManager
//import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroSearchViewModel: ViewModel() {

    private val macroList = MutableLiveData<List<MacroCountModel>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    init {
        load()
    }

    fun load() {
        try {
            FirebaseDBManager.findAll(FirebaseAuth.getInstance().currentUser!!.uid, macroList)
            Timber.i("Retrofit Success : $macroList.value")
        }
        catch (e: Exception) {
            Timber.i("Retrofit error: ${e.message}")
        }
    }

}