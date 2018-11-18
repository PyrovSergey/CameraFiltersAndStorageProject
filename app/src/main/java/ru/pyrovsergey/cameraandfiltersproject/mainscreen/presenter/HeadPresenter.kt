package ru.pyrovsergey.cameraandfiltersproject.mainscreen.presenter

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import ru.pyrovsergey.cameraandfiltersproject.database.CachingDatabase
import ru.pyrovsergey.cameraandfiltersproject.database.TemporaryDatabase
import ru.pyrovsergey.cameraandfiltersproject.utils.BitmapUtils

@InjectViewState
class Presenter : MvpPresenter<HeadView>(), CachingWorkListener {

    fun getCurrentBitmap(): Bitmap? {
        return TemporaryDatabase.currentBitmap
    }

    fun getRotatePhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.rotate(it) }
        TemporaryDatabase.currentBitmap?.let {
            viewState.setImageBitmapResult(it)
            TemporaryDatabase.listOfSavedPaths.add(CachingDatabase.saveBitmap(it, this))
        }
    }

    fun getInvertColorsPhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.invertColors(it) }
        TemporaryDatabase.currentBitmap?.let {
            viewState.setImageBitmapResult(it)
            TemporaryDatabase.listOfSavedPaths.add(CachingDatabase.saveBitmap(it, this))
        }
    }

    fun getMirrorPhoto() {
        TemporaryDatabase.currentBitmap = TemporaryDatabase.currentBitmap?.let { BitmapUtils.flip(it) }
        TemporaryDatabase.currentBitmap?.let {
            viewState.setImageBitmapResult(it)
            TemporaryDatabase.listOfSavedPaths.add(CachingDatabase.saveBitmap(it, this))
        }
    }

    fun checkStateImage() {
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapCurrentState(it) }
    }

    fun showBitmapResult(imageBitmap: Bitmap) {
        TemporaryDatabase.currentBitmap = imageBitmap
        TemporaryDatabase.currentBitmap?.let { viewState.setImageBitmapResult(it) }
    }

    fun getBitmapFromPath(currentImageStringPath: String): Bitmap? {
        return CachingDatabase.loadBitmap(currentImageStringPath, this)
    }

    fun getListOfSavedPaths(): List<String> {
        return TemporaryDatabase.listOfSavedPaths
    }

    override fun onSuccess() {
        viewState.refreshAdapter()
    }
}

interface HeadView : MvpView {
    fun setImageBitmapResult(bitmap: Bitmap)
    fun setImageBitmapCurrentState(bitmap: Bitmap)
    fun refreshAdapter()
}
