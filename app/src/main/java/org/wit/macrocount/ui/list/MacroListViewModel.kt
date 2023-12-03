package org.wit.macrocount.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel

class MacroListViewModel: ViewModel() {

    private val macroList = MutableLiveData<List<MacroCountModel>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    init {
        load()
    }

    fun load() {
        macroList.value = MacroCountManager.findAll()
    }

}