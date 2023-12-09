package org.wit.macrocount.ui.macro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel

class EditMacroViewModel : ViewModel() {

    private val getStatus = MutableLiveData<Boolean>()
    private val addStatus = MutableLiveData<Boolean>()
    private val updateStatus = MutableLiveData<Boolean>()
    private val vmMacro = MutableLiveData<MacroCountModel>()

    val observableGetStatus: LiveData<Boolean>
        get() = getStatus

    val observableAddStatus: LiveData<Boolean>
        get() = addStatus

    val observableUpdateStatus: LiveData<Boolean>
        get() = updateStatus

    val observableMacro: LiveData<MacroCountModel>
        get() = vmMacro

    fun getMacro(id: Long) {
        getStatus.value = try {
            vmMacro.value = MacroCountManager.findById(id)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun addMacro() {
        addStatus.value = try {
            vmMacro.value?.let { MacroCountManager.create(it) }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun updateMacro() {
        updateStatus.value = try {
            vmMacro.value?.let { MacroCountManager.update(it) }
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun editCalories(calories: Int) {
        vmMacro.value?.calories = calories.toString()
    }

    fun editProtein(protein: Int) {
        vmMacro.value?.calories = protein.toString()
    }

    fun editCarbs(carbs: Int) {
        vmMacro.value?.carbs = carbs.toString()
    }

    fun editFat(fat: Int) {
        vmMacro.value?.fat = fat.toString()
    }

    fun setUserId(id: Long) {
        vmMacro.value?.userId = id
    }
}