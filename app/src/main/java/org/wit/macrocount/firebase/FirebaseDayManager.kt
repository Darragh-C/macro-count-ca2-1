package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.DayStore
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber
import java.time.LocalDate

object FirebaseDayManager: DayStore {

    var database: DatabaseReference = FirebaseMacroManager.database

    override fun findAll(macroList: MutableLiveData<List<DayModel>>) {
        //TODO
    }
    override fun findByUserId(userid: String, dayList: MutableLiveData<List<DayModel>>){
        Timber.i("Finding all days for user $userid")

        database.child("user-days").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase day error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<DayModel>()
                    val children = snapshot.children
                    children.forEach {
                        val day = it.getValue(DayModel::class.java)
                        localList.add(day!!)
                    }
                    FirebaseMacroManager.database.child("user-days").child(userid)
                        .removeEventListener(this)

                    dayList.value = localList
                }
            })
    }
    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, day: DayModel) {
        val uid = firebaseUser.value!!.uid
        val key = database.child("days").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        day.uid = key
        val dayValues = day.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/days/$key"] = dayValues
        childAdd["/user-days/$uid/$key"] = dayValues

        database.updateChildren(childAdd)
    }
    override fun addMacroId(macroId: Long, userId: Long, date: LocalDate) {
        //TODO
    }
    override fun findByUserDate(id: Long, date: LocalDate): DayModel? {
        //TODO
    }
    override fun update(userid: String, dayid: String, day: DayModel) {
        val dayValues = day.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["days/$dayid"] = dayValues
        childUpdate["user-days/$userid/$dayid"] = dayValues

        database.updateChildren(childUpdate)
    }
    override fun removeMacro(userId: Long, date: String, macroId: String) {
        //TODO
    }
}