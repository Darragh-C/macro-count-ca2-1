package org.wit.macrocount.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface MacroCountStore {
    fun findAll(macroList: MutableLiveData<List<MacroCountModel>>)
//    fun findAll(userid: String, macroList: MutableLiveData<List<MacroCountModel>>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, macroCount: MacroCountModel)
    fun update(userid: String, macroid: String, macroCount: MacroCountModel)
    fun delete(userid: String, macroid: String)
    fun index(macroCount: MacroCountModel): Int
    fun findByUserId(id: Long): List<MacroCountModel>
    fun findById(id: String): MacroCountModel?
    fun findByTitle(title: String): MacroCountModel
    fun isUniqueTitle(title: String): Boolean
//    fun findByIds(ids: List<String>): List<MacroCountModel?>
}
