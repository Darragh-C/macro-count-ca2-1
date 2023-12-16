package org.wit.macrocount.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
@IgnoreExtraProperties
@Parcelize
data class MacroCountModel(
    var uid: String? = "",
    var title: String = "",
    var description: String = "",
    var calories: String = "0",
    var protein: String = "0",
    var carbs: String = "0",
    var fat: String = "0",
    var userId: Long = 0,
    var image: String = "" ) : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "title" to title,
            "description" to description,
            "calories" to calories,
            "protein" to protein,
            "carbs" to carbs,
            "fat" to fat,
            "userId" to userId,
            "image" to image
        )
    }
}
