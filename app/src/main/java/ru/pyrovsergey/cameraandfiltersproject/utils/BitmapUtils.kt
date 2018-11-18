package ru.pyrovsergey.cameraandfiltersproject.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

internal object BitmapUtils {

    private val NEGATIVE = floatArrayOf(
        -1.0f, 0f, 0f, 0f, 255f, // red
        0f, -1.0f, 0f, 0f, 255f, // green
        0f, 0f, -1.0f, 0f, 255f, // blue
        0f, 0f, 0f, 1.0f, 0f  // alpha
    )

    /**
     * Resamples the captured photo to fit the screen for better memory usage.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be resampled.
     * @return The resampled bitmap
     */
    fun resamplePic(context: Context, imagePath: String): Bitmap {

        // Get device screen size information
        val metrics = DisplayMetrics()
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        manager.defaultDisplay.getMetrics(metrics)

        val targetH = metrics.heightPixels
        val targetW = metrics.widthPixels

        // Get the dimensions of the original bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFile(imagePath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor

        return BitmapFactory.decodeFile(imagePath)
    }

    fun invertColors(inputBitmap: Bitmap): Bitmap {

        val colorFilter = ColorMatrixColorFilter(NEGATIVE)
        val paint = Paint()
        paint.colorFilter = colorFilter


        val resultBitmap = Bitmap.createBitmap(
            inputBitmap.width, inputBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)
        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

        return resultBitmap
    }

    fun grayScale(inputBitmap: Bitmap): Bitmap {

        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val colorFilter = ColorMatrixColorFilter(cm)
        val paint = Paint()
        paint.colorFilter = colorFilter

        val resultBitmap = Bitmap.createBitmap(
            inputBitmap.width, inputBitmap.height,
            Bitmap.Config.RGB_565
        )
        val canvas = Canvas(resultBitmap)

        canvas.drawBitmap(inputBitmap, 0f, 0f, paint)
        return resultBitmap
    }

    fun flip(inputBitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)
        return Bitmap.createBitmap(
            inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height, matrix, true
        )

    }

    fun rotate(inputBitmap: Bitmap): Bitmap {
        val matrix = Matrix()

        matrix.postRotate(90f)

        return Bitmap.createBitmap(inputBitmap, 0, 0, inputBitmap.width, inputBitmap.height, matrix, true)
    }

    /**
     * Creates the temporary image file in the cache directory.
     *
     * @return The temporary image file.
     * @throws IOException Thrown if there is an error creating the file
     */
    @Throws(IOException::class)
    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = context.externalCacheDir

        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
    }

    /**
     * Deletes image file for a given path.
     *
     * @param context   The application context.
     * @param imagePath The path of the photo to be deleted.
     */
    fun deleteImageFile(context: Context, imagePath: String): Boolean {
        // Get the file
        val imageFile = File(imagePath)

        // Delete the image
        return imageFile.delete()
    }

    /**
     * Helper method for adding the photo to the system photo gallery so it can be accessed
     * from other apps.
     *
     * @param imagePath The path of the saved image
     */
    private fun galleryAddPic(context: Context, imagePath: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(imagePath)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }


    /**
     * Helper method for saving the image.
     *
     * @param context The application context.
     * @param image   The image to be saved.
     * @return The path of the saved image.
     */
    fun saveImage(context: Context, image: Bitmap): String? {

        var savedImagePath: String? = null

        // Create the new file in the external storage
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Editor"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }

        // Save the new Bitmap
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Add the image to the system gallery
            galleryAddPic(context, savedImagePath)

            // Show a Toast with the save location
            val savedMessage = "Image Saved"
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
        }
        return savedImagePath
    }

}