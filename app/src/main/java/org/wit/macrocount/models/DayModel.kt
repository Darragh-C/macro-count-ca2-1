package org.wit.macrocount.models
import java.time.LocalDate
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
@IgnoreExtraProperties
@Parcelize
data class DayModel(
    var uid: String? = "",
    var date: String = "",
    var userMacroIds: List<String> = emptyList<String>()) : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "date" to date,
            "userMacroIds" to userMacroIds,
        )
    }
}
