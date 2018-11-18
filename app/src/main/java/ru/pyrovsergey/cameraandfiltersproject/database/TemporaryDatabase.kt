package ru.pyrovsergey.cameraandfiltersproject.database

import android.graphics.Bitmap

object TemporaryDatabase {
    var currentBitmap: Bitmap? = null
    val listOfSavedPaths = mutableListOf<String>()
}