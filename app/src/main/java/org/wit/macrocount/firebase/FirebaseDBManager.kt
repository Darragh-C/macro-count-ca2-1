package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.models.MacroCountStore
import timber.log.Timber

object FirebaseDBManager: MacroCountStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(macroList: MutableLiveData<List<MacroCountModel>>) {
        database.child("macrocounts")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<MacroCountModel>()
                    val children = snapshot.children
                    children.forEach {
                        val macro = it.getValue(MacroCountModel::class.java)
                        localList.add(macro!!)
                    }
                    database.child("macrocounts")
                        .removeEventListener(this)

                    macroList.value = localList
                }
            })
    }

    override fun findAll(userid: String, macroList: MutableLiveData<List<MacroCountModel>>) {
        Timber.i("Finding all macrocounts for user $userid")

        database.child("user-macrocounts").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<MacroCountModel>()
                    val children = snapshot.children
                    children.forEach {
                        val macro = it.getValue(MacroCountModel::class.java)
                        localList.add(macro!!)
                    }
                    database.child("user-macrocounts").child(userid)
                        .removeEventListener(this)

                    macroList.value = localList
                }
            })
    }

    override fun findById(userid: String, macroid: String, macro: MutableLiveData<MacroCountModel>) {

        database.child("user-macrocounts").child(userid)
            .child(macroid).get().addOnSuccessListener {
                macro.value = it.getValue(MacroCountModel::class.java)
                Timber.i("firebase Got macro it.value ${it.value}")
                Timber.i("firebase Got macro macro.value ${macro.value}")
                Timber.i("firebase Got macro ${macro}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, macroCount: MacroCountModel) {
        Timber.i("Creating macro FirebaseDBManager : $database")
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("macrocounts").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        macroCount.uid = key
        val macroValues = macroCount.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/macrocounts/$key"] = macroValues
        childAdd["/user-macrocounts/$uid/$key"] = macroValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, macroid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/macrocounts/$macroid"] = null
        childDelete["/user-macrocounts/$userid/$macroid"] = null

        database.updateChildren(childDelete)
    }

    override fun index(macroCount: MacroCountModel): Int {
        TODO("Not yet implemented")
    }

    override fun findByUserId(id: Long): List<MacroCountModel> {
        TODO("Not yet implemented")
    }

//    override fun findById(id: Long): MacroCountModel? {
//        TODO("Not yet implemented")
//    }

    override fun findByTitle(title: String): MacroCountModel {
        TODO("Not yet implemented")
    }

    override fun isUniqueTitle(title: String): Boolean {
        TODO("Not yet implemented")
    }

//    override fun findByIds(ids: List<String>): List<MacroCountModel?> {
//        TODO("Not yet implemented")
//    }

    override fun update(userid: String, macroid: String, macroCount: MacroCountModel) {
        val macroValues = macroCount.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["macrocounts/$macroid"] = macroValues
        childUpdate["user-macrocounts/$userid/$macroid"] = macroValues

        database.updateChildren(childUpdate)
    }

}