package ru.pyrovsergey.cameraandfiltersproject.mainscreen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import ru.pyrovsergey.cameraandfiltersproject.R
import ru.pyrovsergey.cameraandfiltersproject.constants.*
import ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter.HeadView
import ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter.Presenter
import ru.pyrovsergey.cameraandfiltersproject.utils.PermissionsUtils


class MainActivity : MvpAppCompatActivity(), HeadView {

    @InjectPresenter
    lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currentImage.setImageBitmap(presenter.getCurrentBitmap())
        checkCurrentStateImage()
        checkCurrentStatePermission()
    }

    private fun checkCurrentStateImage() {
        presenter.checkStateImage()
    }

    private fun checkCurrentStatePermission() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            true -> PermissionsUtils.requestStoragePermission(this, CONSTANT_STORAGE)
            false -> PermissionsUtils.requestCameraPermission(this, CONSTANT_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CONSTANT_CAMERA ->
                when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    true -> PermissionsUtils.requestStoragePermission(this, CONSTANT_STORAGE)
                    false -> {
                        PermissionsUtils.requestStoragePermission(this, CONSTANT_STORAGE)
                        firstTimeButton.isClickable = false
                    }
                }
            CONSTANT_STORAGE ->
                when (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    false -> {
                        firstTimeButton.isClickable = false
                        imageButtonRotate.isClickable = false
                        imageButtonInvertColors.isClickable = false
                        imageButtonMirrorImage.isClickable = false
                    }
                }
        }
    }

    fun chooseAction(view: View) {
        showChooseDialog()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(resultCode) {
            Activity.RESULT_OK -> {
                when(requestCode) {
                    CONSTANT_REQUEST_IMAGE_CAPTURE -> {
                        firstTimeButton.visibility = View.INVISIBLE
                        cardViewImage.visibility = View.VISIBLE
                        val imageBitmap = data!!.extras.get(CONSTANT_DATA) as Bitmap
                        presenter.showBitmapResult(imageBitmap)
                    }
                    CONSTANT_REQUEST_GALLERY_PICTURE -> {
                        val selectedImagePath: String
                        val bitmap: Bitmap
                        if (data != null) {
                            val selectedImage = data.data
                            val filePath = arrayOf(MediaStore.Images.Media.DATA)
                            val cursor = contentResolver.query(selectedImage!!, filePath, null, null, null)
                            cursor!!.moveToFirst()
                            val columnIndex = cursor.getColumnIndex(filePath[0])
                            selectedImagePath = cursor.getString(columnIndex)
                            cursor.close()
                            if (selectedImagePath != null) {
                                Log.d("Main", selectedImagePath)
                            }
                            bitmap = BitmapFactory.decodeFile(selectedImagePath) // load

                            cardViewImage.visibility = View.VISIBLE
                            presenter.showBitmapResult(bitmap)
                        } else {
                            Toast.makeText(applicationContext, "Cancelled",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showChooseDialog() {
        val myAlertDialog = AlertDialog.Builder(
                this)
        myAlertDialog.setTitle("Upload Pictures Option")
        myAlertDialog.setMessage("How do you want to set your picture?")

        myAlertDialog.setPositiveButton("Gallery"
        ) { arg0, arg1 ->
            val pictureActionIntent = Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(
                    pictureActionIntent,
                    CONSTANT_REQUEST_GALLERY_PICTURE)
        }

        myAlertDialog.setNegativeButton("Camera"
        ) { arg0, arg1 ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, CONSTANT_REQUEST_IMAGE_CAPTURE)
                }
            }
        }
        myAlertDialog.show()
    }

    fun rotatePhoto(view: View) {
        presenter.getRotatePhoto()
    }

    fun invertPhoto(view: View) {
        presenter.getInvertColorsPhoto()
    }

    fun mirrorPhoto(view: View) {
        presenter.getMirrorPhoto()
    }

    override fun setImageBitmapResult(bitmap: Bitmap) {
        currentImage.setImageBitmap(bitmap)
    }

    override fun setImageBitmapCurrentState(bitmap: Bitmap) {
        cardViewImage.visibility = View.VISIBLE
        firstTimeButton.visibility = View.INVISIBLE
        currentImage.setImageBitmap(bitmap)
    }
}
