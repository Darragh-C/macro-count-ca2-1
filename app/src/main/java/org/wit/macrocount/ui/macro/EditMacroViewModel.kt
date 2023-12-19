package org.wit.macrocount.ui.macro

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseDBManager
import org.wit.macrocount.firebase.FirebaseImageManager
import org.wit.macrocount.models.DayManager
//import org.wit.macrocount.models.MacroCountManager
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
    lateinit var bitmap: Bitmap


    val observableGetStatus: LiveData<Boolean>
        get() = getStatus

    val observableCalories: LiveData<Int>
        get() = vmMacro.value?.calories?.toInt()?.let { MutableLiveData(it) } ?: MutableLiveData(0)

    val observableProtein: LiveData<Int>
        get() = vmMacro.value?.protein?.toInt()?.let { MutableLiveData(it) } ?: MutableLiveData(0)

    val observableCarbs: LiveData<Int>
        get() = vmMacro.value?.carbs?.toInt()?.let { MutableLiveData(it) } ?: MutableLiveData(0)

    val observableFat: LiveData<Int>
        get() = vmMacro.value?.fat?.toInt()?.let { MutableLiveData(it) } ?: MutableLiveData(0)

    val observableAddStatus: LiveData<Boolean>
        get() = addStatus

    val observableUpdateStatus: LiveData<Boolean>
        get() = updateStatus

    val observableMacro: LiveData<MacroCountModel>
        get() = vmMacro

    val observableCopy: LiveData<MacroCountModel>
        get() = copiedMacro


    fun getMacro(userid: String, macroid: String) {
        try {
            FirebaseDBManager.findById(userid, macroid, vmMacro)
            Timber.i("Edit macro getMacro() Success : ${
                vmMacro.value}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : $e.message")
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

    fun addMacro(firebaseUser: MutableLiveData<FirebaseUser>, macro: MacroCountModel) {
        Timber.i("adding macro at edit macro vm, macro: ${macro}, user: ${firebaseUser.value.toString()}")
        addStatus.value = try {
            FirebaseDBManager.create(firebaseUser, macro)
            val createdMacro = FirebaseDBManager.findById(firebaseUser.value!!.uid, vmMacro.value?.uid!!, vmMacro)

            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun updateMacro(userid:String, macroid: String, macro: MacroCountModel) {
        try {
            FirebaseDBManager.update(userid, macroid, macro)
            Timber.i("Detail update() Success : userid: $userid , macroid: $macroid , macro: $macro")
        }
        catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }

    fun uploadImage(filename: String, bitmap: Bitmap, updating : Boolean) {
        FirebaseImageManager
            .uploadMacroImageToFirebase(
                observableMacro.value?.uid!!,
                bitmap,
                true
            )
    }

    fun editTitle(string: String) {
        vmMacro.value?.title = string
    }

    fun editCalories(calories: Int) {
        vmMacro.value?.calories = calories.toString()
        vmMacro.value = vmMacro.value
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

    fun refresh() {
        vmMacro.value = vmMacro.value
    }
}