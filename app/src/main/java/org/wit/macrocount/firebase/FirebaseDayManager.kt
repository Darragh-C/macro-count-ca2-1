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

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

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
        //TODO
    }
    override fun addMacroId(macroId: Long, userId: Long, date: LocalDate) {
        //TODO
    }
    override fun findByUserDate(id: Long, date: LocalDate): DayModel? {
        //TODO
    }
    override fun update(userid: String, dayid: String, day: DayModel) {
        //TODO
    }
    override fun removeMacro(userId: Long, date: String, macroId: String) {
        //TODO
    }
}