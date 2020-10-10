package ru.andrewkir.vtbmobile

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.FullScreen)
        setContentView(R.layout.activity_main)

        cardView.visibility = View.INVISIBLE
        closeButton.visibility = View.INVISIBLE

        createLoan.setOnClickListener {
            var intent = Intent(this, CreditActivity::class.java)
            val options =
                ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            this.startActivity(intent, options.toBundle())
            camera.onStop()
        }

        closeButton.setOnClickListener {
            takePhotoButton.isEnabled = true
            accountButton.isEnabled = true
            helpButton.isEnabled = true

            closeButton.visibility = View.INVISIBLE
            cardView.visibility = View.INVISIBLE
            val animFadeOut = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_out
            )
            cardView.startAnimation(animFadeOut)

            val colorFrom = Color.parseColor("#99000000")
            val colorTo = Color.parseColor("#00000000")
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 1000 // milliseconds

            colorAnimation.addUpdateListener { animator ->
                cameraRoot.background = ColorDrawable(animator.animatedValue as Int)
                cameraRoot.foreground = ColorDrawable(animator.animatedValue as Int)
            }
            colorAnimation.start()
        }

        takePhotoButton.setOnClickListener {

            takePhotoButton.isEnabled = false
            accountButton.isEnabled = false
            helpButton.isEnabled = false

            closeButton.visibility = View.VISIBLE

            cardView.visibility = View.VISIBLE
            val animFadein = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.fade_in
            )
            cardView.startAnimation(animFadein)

            val colorFrom = Color.parseColor("#00000000")
            val colorTo = Color.parseColor("#99000000")
            val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
            colorAnimation.duration = 1000 // milliseconds

            colorAnimation.addUpdateListener { animator ->
                cameraRoot.background = ColorDrawable(animator.animatedValue as Int)
                cameraRoot.foreground = ColorDrawable(animator.animatedValue as Int)
            }
            colorAnimation.start()

//            camera.captureImage(CameraKitView.ImageCallback { _, bytes ->
//                val relativeLocation =
//                    Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
//                val contentValues = ContentValues().apply {
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
//                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
//                        put(MediaStore.MediaColumns.IS_PENDING, 1)
//                    }
//                }
//
//                val resolver = this.contentResolver
//                val uri = resolver.insert(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    contentValues
//                )
//                try {
//
//                    uri?.let { uri ->
//                        val stream = resolver.openOutputStream(uri)
//                        stream?.write(bytes)
//
//                        var mediaScanIntent = Intent(
//                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
//                        );
//                        mediaScanIntent.data = uri
//                        this.sendBroadcast(mediaScanIntent)
//                    } ?: throw IOException("Failed to create new MediaStore record")
//                } catch (e: IOException) {
//                    if (uri != null) {
//                        resolver.delete(uri, null, null)
//                    }
//                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
//                } finally {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
//                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
//                }
//            })
        }
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
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
