package org.wit.macrocount.ui.macro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseDBManager
import org.wit.macrocount.models.DayManager
import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber
import java.time.LocalDate

class EditMacroViewModel : ViewModel() {

    private val vmMacro = MutableLiveData<MacroCountModel>()
    private val copiedMacro = MutableLiveData<MacroCountModel>()

    private val getStatus = MutableLiveData<Boolean>()
    private val addStatus = MutableLiveData<Boolean>()
    private val updateStatus = MutableLiveData<Boolean>()

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    val seekbarMin = 0
    val seekbarMax = 500


    val observableGetStatus: LiveData<Boolean>
        get() = getStatus

    val observableAddStatus: LiveData<Boolean>
        get() = addStatus

    val observableUpdateStatus: LiveData<Boolean>
        get() = updateStatus

    val observableMacro: LiveData<MacroCountModel>
        get() = vmMacro

    val observableCopy: LiveData<MacroCountModel>
        get() = copiedMacro


    fun getMacro(id: Long) {
        getStatus.value = try {
            vmMacro.value = MacroCountManager.findById(id)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun setMacro(macro: MacroCountModel) {
        vmMacro.value = macro
        vmMacro.value = vmMacro.value
    }

    fun addToDay(userId: Long) {
//        vmMacro.value?.let { DayManager.addMacroId(it.uid, it.userId, LocalDate.now() ) }
        Timber.i("addtoDay")
    }

    fun setCopy(macro: MacroCountModel) {
        copiedMacro.value = macro
    }

    fun addMacro(firebaseUser: MutableLiveData<FirebaseUser>) {
        addStatus.value = try {
            vmMacro.value?.let { FirebaseDBManager.create(firebaseUser, it) }
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

    fun editTitle(string: String) {
        vmMacro.value?.title = string
    }

    fun editCalories(calories: Int) {
        vmMacro.value?.calories = calories.toString()
    }

    fun editProtein(protein: Int) {
        vmMacro.value?.protein = protein.toString()
        vmMacro.value = vmMacro.value
    }

    fun editCarbs(carbs: Int) {
        vmMacro.value?.carbs = carbs.toString()
        vmMacro.value = vmMacro.value
    }

    fun editFat(fat: Int) {
        vmMacro.value?.fat = fat.toString()
        vmMacro.value = vmMacro.value
    }

    fun setUserId(id: Long) {
        vmMacro.value?.userId = id

    }
}