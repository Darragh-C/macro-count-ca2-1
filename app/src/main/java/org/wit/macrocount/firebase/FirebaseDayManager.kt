package org.wit.macrocount.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import org.wit.macrocount.models.DayModel
import org.wit.macrocount.models.DayStore
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
    override fun findByUserId(userid: String, callback: (List<DayModel?>) -> Unit){
        Timber.i("Finding all days for user $userid")

        database.child("user-days").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase day error : ${error.message}")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        Timber.i("Child Key: ${childSnapshot.key}, Value: ${childSnapshot.value}")
                    }
                    val localList = ArrayList<DayModel>()
                    val children = snapshot.children

                    children.forEach {

                        val day = it.getValue(DayModel::class.java)
                        Timber.i("Adding child day : $day")
                        localList.add(day!!)
                        Timber.i("Local list : ${localList}")
                    }
                    FirebaseMacroManager.database.child("user-days").child(userid)
                        .removeEventListener(this)
                    Timber.i("Initiating callback with localList : ${localList}")
                    callback(localList)
                }
            })
    }

    fun snapshotCheck(userid: String, callback: (Boolean) -> Unit) {
        Timber.i("Checking day snapshot for user $userid")

        val userDaysRef = database.child("user-days").child(userid)

        userDaysRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase day error: ${error.message}")
                callback(false)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                Timber.i("Snapshot : $snapshot")

                if (snapshot.exists()) {
                    Timber.i("Snapshot exists")
                    callback(true)
                } else {
                    Timber.i("Snapshot does not exist, creating day")

                    // Create a new DayModel for the current day
                    val newDayModel = DayModel()
                    newDayModel.date = LocalDate.now().toString()

                    // Use the create function to store the new DayModel
                    Timber.i("creating today with newDayModel: $newDayModel")
                    create(firebaseAuth.currentUser!!, newDayModel) { createdResult ->
                        if (createdResult) {
                            Timber.i("Created day node")
                            callback(true)
                        } else {
                            Timber.i("Error creating day")
                            callback(false)
                        }
                    }
                }
            }
        })
    }

    override fun create(firebaseUser: FirebaseUser, day: DayModel, callback: (Boolean) -> Unit) {
        val uid = firebaseUser.uid

        val key = database.child("user-days").push().key
        Timber.i("Creating day with key : $key")
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        day.uid = key
        Timber.i("Creating day $day")
        val dayValues = day.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/user-days/$uid/$key"] = dayValues

        database.updateChildren(childAdd)
        callback(true)
    }
    override fun addMacroId(macroId: String, firebaseUser: FirebaseUser, date: LocalDate) {
        Timber.i("Adding macro id to day")
        val userId = firebaseUser.uid
        var days: List<DayModel?> = emptyList()

        findByUserId(userId) {result ->
            Timber.i("addMacroId days result : ${result}")
            days = result
            Timber.i("addMacroId days : ${days}")
            val stringDate = date.toString()
            Timber.i("Date format : ${stringDate}, date type : ${stringDate::class}")
            //gets a list of days that match today's date
            val today = days.filter { d ->
                Timber.i("d.date format : ${d?.date}, d.date type : ${d?.date!!::class}")
                d.date == date.toString()
            }

            var foundToday = today[0]
            Timber.i("Updating foundToday : ${foundToday?.uid}")
            var macroIds = foundToday?.userMacroIds!!.toMutableList()
            macroIds.add(macroId)
            foundToday.userMacroIds = macroIds
            update(userId, foundToday.uid!!, foundToday)

        }
    }
    override fun findByUserDate(userid: String, date: LocalDate, callback: (DayModel?) -> Unit) {
        Timber.i("finding by user date ${date.toString()}")
        var day = DayModel()
        var days = MutableLiveData<List<DayModel?>>()
        findByUserId(userid) {result ->
            days.value = result

            Timber.i("findByUserDate days : ${days.value}")
            val dayArray = days.value?.filter { d -> d?.date == date.toString() }
            Timber.i("findByUserDate dayArray : ${dayArray}")
            Timber.i("findByUserDate dayArray 0 : ${dayArray?.get(0)}")
            callback(dayArray?.get(0))

        }
    }

    override fun update(userid: String, dayid: String, day: DayModel) {
        Timber.i("Updating day : $dayid")
        val dayValues = day.toMap()
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["user-days/$userid/$dayid"] = dayValues

        database.updateChildren(childUpdate)
    }
    override fun removeMacro(userid: String, date: String, macroId: String) {
        Timber.i("Removing macro $macroId from day $date for user $userid")
        //find day
        findByUserDate(userid, LocalDate.parse(date)) { result ->
            //remove macro from day
            var macro = result!!
            var macroIds = macro.userMacroIds
            var updatedMacroIds = macroIds.filter { m -> m != macroId }
            macro.userMacroIds = updatedMacroIds
            //update day
            update(userid, result.uid!!, macro)
        }
    }

    override fun checkDayExists(userid: String, date: LocalDate, callback: (Boolean) -> Unit) {
        Timber.i("Checking user days for $userid")
        var days = MutableLiveData<List<DayModel?>>()
        findByUserId(userid) {result ->
            days.value = result
            Timber.i("findByUserDate days : ${days.value}")
            val dayArray = days.value?.filter { d -> d?.date == date.toString() }
            Timber.i("findByUserDate dayArray : ${dayArray}")
            if (dayArray != null) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }


}