package org.wit.macrocount.models
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
@IgnoreExtraProperties
@Parcelize
data class UserModel (var uid: String? = "",
                      var name: String = "",
                      var gender: String = "",
                      var age: String = "0",
                      var weight: String = "0",
                      var height: String = "0",
                      var dob: String = "",
                      var goal: String = "" ) : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "gender" to gender,
            "age" to age,
            "weight" to weight,
            "height" to height,
            "dob" to dob,
            "goal" to goal,
        )
    }
}
