package ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import ru.pyrovsergey.cameraandfiltersproject.temporarydatabase.TemporaryDatabase
import ru.pyrovsergey.cameraandfiltersproject.utils.BitmapUtils

@InjectViewState
class Presenter : MvpPresenter<HeadView>() {
    fun getCurrentBitmap(): Bitmap? {
        return TemporaryDatabase.currentBitmap
    }

    fun setCurrentBitmap(bitmap: Bitmap) {
        TemporaryDatabase.currentBitmap = bitmap
    }

    fun getRotatePhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.rotate(it) }
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapResult(it) }
    }

    fun getInvertColorsPhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.invertColors(it) }
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapResult(it) }
    }

    fun getMirrorPhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.flip(it) }
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapResult(it) }
    }

    fun checkStateImage() {
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapCurrentState(it) }
    }
}

interface HeadView : MvpView {
    fun setImageBitmapResult(bitmap: Bitmap)
    fun setImageBitmapCurrentState(bitmap: Bitmap)
}
