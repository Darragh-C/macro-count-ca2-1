package org.wit.macrocount.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class MacroCountModel(var id: Long = 0,
                           var title: String = "",
                           var description: String = "",
                           var calories: String = "",
                           var protein: String = "",
                           var carbs: String = "",
                           var fat: String = "",
                           var userId: Long = 0,
                           var image: Uri = Uri.EMPTY ) : Parcelable
