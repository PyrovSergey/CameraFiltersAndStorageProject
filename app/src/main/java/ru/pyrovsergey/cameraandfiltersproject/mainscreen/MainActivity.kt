package ru.pyrovsergey.cameraandfiltersproject.mainscreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.view.View
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_main.*
import ru.pyrovsergey.cameraandfiltersproject.R
import ru.pyrovsergey.cameraandfiltersproject.constants.CONSTANT_CAMERA
import ru.pyrovsergey.cameraandfiltersproject.constants.CONSTANT_STORAGE
import ru.pyrovsergey.cameraandfiltersproject.constants.REQUEST_IMAGE_CAPTURE
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
        PermissionsUtils.requestCameraPermission(this, CONSTANT_CAMERA)
    }

    private fun checkCurrentStateImage() {
        presenter.checkStateImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONSTANT_CAMERA)
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
            )
                PermissionsUtils.requestStoragePermission(this, CONSTANT_STORAGE)
    }

    fun chooseAction(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            firstTimeButton.visibility = View.INVISIBLE
            cardViewImage.visibility = View.VISIBLE
            val imageBitmap = data!!.extras.get("data") as Bitmap
            presenter.setCurrentBitmap(imageBitmap)
            currentImage.setImageBitmap(presenter.getCurrentBitmap())
        }
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
