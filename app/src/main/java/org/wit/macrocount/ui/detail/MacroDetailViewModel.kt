package org.wit.macrocount.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel

class MacroDetailViewModel : ViewModel() {
    private val macro = MutableLiveData<MacroCountModel>()

    val observableMacro: LiveData<MacroCountModel>
        get() = macro

    fun getMacro(id: Long) {
        macro.value = MacroCountManager.findById(id)
    }
}