package org.wit.macrocount

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.wit.macrocount.R

fun showImagePicker(intentLauncher : ActivityResultLauncher<Intent>) {
    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "image/*"
    chooseFile = Intent.createChooser(chooseFile, R.string.select_macrocount_image.toString())
    intentLauncher.launch(chooseFile)
}