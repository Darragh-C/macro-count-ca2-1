package org.wit.macrocount.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

interface DayStore {

    fun findAll(macroList: MutableLiveData<List<DayModel>>)
    fun findByUserId(userid: String, dayList: MutableLiveData<List<DayModel>>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, day: DayModel)
    fun addMacroId(macroId: Long, userId: Long, date: LocalDate)
    fun findByUserDate(id: Long, date: LocalDate): DayModel?
    fun update(userid: String, dayid: String, day: DayModel)
    fun removeMacro(userId: Long, date: String, macroId: String)
}