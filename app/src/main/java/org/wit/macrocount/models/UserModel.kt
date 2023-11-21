package org.wit.macrocount.models
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class UserModel (var id: Long = 0,
                      var name: String = "",
                      var gender: String = "",
                      var age: String = "0",
                      var weight: String = "0",
                      var height: String = "0",
                      var dob: String = "",
                      var goal: String = "",
                      var email: String = "",
                      var password: String = "") : Parcelable

