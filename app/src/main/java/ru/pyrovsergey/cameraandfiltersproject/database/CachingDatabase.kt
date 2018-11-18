package ru.pyrovsergey.cameraandfiltersproject.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import ru.pyrovsergey.cameraandfiltersproject.constants.CONSTANT_TIME_PATTERN
import ru.pyrovsergey.cameraandfiltersproject.di.App
import ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter.CachingWorkListener
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object CachingDatabase {

    fun saveBitmap(bitmap: Bitmap, listener: CachingWorkListener): String {
        val timeStamp = SimpleDateFormat(CONSTANT_TIME_PATTERN).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = App.instance.cacheDir
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            try {
                val fOut = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                Log.d("CachingDatabase", imageFileName)
                listener.onSuccess()
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return imageFileName
    }

    fun loadBitmap(timeString: String, listener: CachingWorkListener): Bitmap? {
        //val imageFileName = "JPEG_$timeString.jpg"
        var bmp: Bitmap? = null
        val storageDir = App.instance.cacheDir
        if (storageDir.exists()) {
            listener.onSuccess()
            Log.d("CachingDatabase", storageDir.path + "/" + timeString)
            bmp = BitmapFactory.decodeFile(storageDir.path + "/" + timeString)
        }
        return bmp
    }
}