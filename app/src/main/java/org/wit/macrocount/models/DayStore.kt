package org.wit.macrocount.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

interface DayStore {

    fun findAll(macroList: MutableLiveData<List<DayModel>>)
    fun findByUserId(userid: String, callback: (List<DayModel?>) -> Unit)
    fun create(firebaseUser: FirebaseUser, day: DayModel, callback: (Boolean) -> Unit)
    fun addMacroId(macroId: String, firebaseUser: FirebaseUser, date: LocalDate)
    fun findByUserDate(id: String, date: LocalDate, callback: (DayModel?) -> Unit)
    fun update(userid: String, dayid: String, day: DayModel)
    fun removeMacro(userid: String, date: String, macroId: String)
    fun checkDayExists(userid: String, date: LocalDate, callback: (Boolean) -> Unit)
}