package ru.pyrovsergey.cameraandfiltersproject.utils

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity

class PermissionsUtils {
    companion object {
        fun requestCameraPermission(activity: AppCompatActivity, request: Int) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA), request)
        }

        fun hasStoragePermission(activity: AppCompatActivity): Boolean {
            return ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestStoragePermission(activity: AppCompatActivity, request: Int) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), request)
        }
    }
}