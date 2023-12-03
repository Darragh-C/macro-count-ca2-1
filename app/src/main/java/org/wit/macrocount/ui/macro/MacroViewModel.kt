package org.wit.macrocount.ui.macro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel

class MacroViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addMacro(macro: MacroCountModel) {
        status.value = try {
            MacroCountManager.create(macro)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}