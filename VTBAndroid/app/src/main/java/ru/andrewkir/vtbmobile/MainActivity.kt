package ru.andrewkir.vtbmobile

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_main)

        takePhotoButton.setOnClickListener {
            camera.captureImage(CameraKitView.ImageCallback { cameraKitView, bytes ->
                val relativeLocation = Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val resolver = this.contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                try {

                    uri?.let { uri ->
                        val stream = resolver.openOutputStream(uri)
                        stream?.write(bytes)

                        var mediaScanIntent = Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.data = uri
                        this.sendBroadcast(mediaScanIntent);
                        Toast.makeText(this, "pic saved", Toast.LENGTH_SHORT).show()
                    } ?: throw IOException("Failed to create new MediaStore record")
                } catch (e: IOException) {
                    if (uri != null) {
                        resolver.delete(uri, null, null)
                    }
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

    override fun onPause() {
        camera.onPause()
        super.onPause()
    }

    override fun onStop() {
        camera.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
