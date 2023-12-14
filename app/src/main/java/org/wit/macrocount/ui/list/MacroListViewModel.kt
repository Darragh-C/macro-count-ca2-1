package org.wit.macrocount.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroListViewModel: ViewModel() {

    private val macroList = MutableLiveData<List<MacroCountModel>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    init {
        load()
    }

    fun load() {
        try {
            MacroCountManager.findAll(macroList)
            Timber.i("Retrofit Success : $macroList.value")
        }
        catch (e: Exception) {
            Timber.i("Retrofit error: ${e.message}")
        }
    }

}