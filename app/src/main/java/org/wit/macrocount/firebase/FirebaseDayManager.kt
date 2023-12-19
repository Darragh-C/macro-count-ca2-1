package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.DayStore
import org.wit.macrocount.models.MacroCountModel
import org.wit.macrocount.ui.login.LoggedInViewModel
import timber.log.Timber
import java.time.LocalDate

object FirebaseDayManager: DayStore {

    var database: DatabaseReference = FirebaseMacroManager.database
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var loggedInViewModel: LoggedInViewModel


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
        val key = database.child("user-days").push().key
        Timber.i("Creating day with key : $key")
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        day.uid = key
        val dayValues = day.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/user-days/$uid/$key"] = dayValues

        database.updateChildren(childAdd)
    }
    override fun addMacroId(macroId: String, firebaseUser: MutableLiveData<FirebaseUser>, date: LocalDate) {
        Timber.i("Adding macro id to day")
        val userId = firebaseUser.value!!.uid
        var days = MutableLiveData<List<DayModel>>()
        findByUserId(userId, days)
        var today = days.value?.filter { d -> d.date == date.toString() }
        if (today != null) {
            var foundToday = today[0]
            Timber.i("Updating foundToday : ${foundToday.uid}")
            var macroIds = foundToday.userMacroIds.toMutableList()
            macroIds.add(macroId)
            foundToday.userMacroIds = macroIds
            update(userId, foundToday.uid!!, foundToday)
        } else {
            Timber.i("Day not found, creating new one")
            var newDayModel = DayModel()
            newDayModel.date = date.toString()
            newDayModel.userMacroIds = listOf(macroId)

            create(firebaseUser, newDayModel)
        }
    }


    //    override fun findByUserDate(id: Long, date: LocalDate): DayModel? {
//        //TODO
//        return DayModel()
//    }
    override fun update(userid: String, dayid: String, day: DayModel) {
        Timber.i("Updating day : $dayid")
        val dayValues = day.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["user-days/$userid/$dayid"] = dayValues

        database.updateChildren(childUpdate)
    }
    override fun removeMacro(userId: Long, date: String, macroId: String) {
        //TODO
    }

    private fun getCurrentUserId(): String? {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        return currentUser?.uid
    }
}