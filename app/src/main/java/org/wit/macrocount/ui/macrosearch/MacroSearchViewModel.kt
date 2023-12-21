package org.wit.macrocount.ui.macrosearch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.wit.macrocount.firebase.FirebaseMacroManager
import org.wit.macrocount.firebase.FirebaseProfileManager
//import org.wit.macrocount.models.MacroCountManager
import org.wit.macrocount.models.MacroCountModel
import timber.log.Timber

class MacroSearchViewModel: ViewModel() {

    private val macroList = MutableLiveData<List<MacroCountModel>>()
    var readOnly = MutableLiveData(false)
    var favourites = MutableLiveData<List<String>>()

    val observableMacroList: LiveData<List<MacroCountModel>>
        get() = macroList

    val observableFavourites: MutableLiveData<List<String>>
        get() = favourites

    init {
        getFavourites(FirebaseAuth.getInstance().currentUser!!, favourites)
        load()
    }

    fun load() {
        try {
            readOnly.value = false
            FirebaseMacroManager.findAll(FirebaseAuth.getInstance().currentUser!!.uid, macroList)
            Timber.i("Retrofit Success : $macroList.value")
        }
        catch (e: Exception) {
            Timber.i("Retrofit error: ${e.message}")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseMacroManager.findAll(macroList)
            Timber.i("Report LoadAll Success : ${macroList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report LoadAll Error : $e.message")
        }
    }

    fun handleFavourite(macroCount: MacroCountModel, isFavourite: Boolean, firebaseUser: FirebaseUser) {
        //add to user's favs
        if (isFavourite) {
            Timber.i("Adding macro to favourites")
            FirebaseProfileManager.addFavourite(macroCount.uid!!, firebaseUser)
        } else {
            Timber.i("Removing macro from favourites")
            FirebaseProfileManager.removeFavourite(macroCount.uid!!, firebaseUser)
        }
    }

    fun getFavourites(firebaseUser: FirebaseUser, favourites: MutableLiveData<List<String>>) {
        Timber.i("Getting favourites list view model")
        FirebaseProfileManager.getFavourites(firebaseUser, favourites)
    }

}