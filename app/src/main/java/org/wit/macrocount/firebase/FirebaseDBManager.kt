package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.MacroCountStore

object FirebaseDBManager: MacroCountStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun findAll(macroList: MutableLiveData<List<MacroCountModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, macroList: MutableLiveData<List<MacroCountModel>>) {
        TODO("Not yet implemented")
    }

//    override fun findById(userid: String, macroid: String, macroCount: MutableLiveData<MacroCountModel>) {
//        TODO("Not yet implemented")
//    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, macroCount: MacroCountModel) {
        TODO("Not yet implemented")
    }

    override fun delete(userid: String, macroid: String) {
        TODO("Not yet implemented")
    }

    override fun index(macroCount: MacroCountModel): Int {
        TODO("Not yet implemented")
    }

    override fun findByUserId(id: Long): List<MacroCountModel> {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): MacroCountModel? {
        TODO("Not yet implemented")
    }

    override fun findByTitle(title: String): MacroCountModel {
        TODO("Not yet implemented")
    }

    override fun isUniqueTitle(title: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun findByIds(ids: List<String>): List<MacroCountModel?> {
        TODO("Not yet implemented")
    }

    override fun update(userid: String, macroid: String, macroCount: MacroCountModel) {
        TODO("Not yet implemented")
    }
}