package ru.andrewkir.vtbmobile

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.camerakit.CameraKitView
import com.google.gson.JsonArray
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.CarPhoto
import java.io.IOException

class CameraFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_camera, container, false)
    }

    override fun onStart() {
        super.onStart()
        camera.onStart()
        activity!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardView.visibility = View.INVISIBLE
        closeButton.visibility = View.INVISIBLE


        createLoan.setOnClickListener {
            var intent = Intent(activity, CreditActivity::class.java)
            val options =
                ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
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
                activity?.applicationContext,
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

        progressBar.visibility = View.INVISIBLE

        takePhotoButton.setOnClickListener {

            takePhotoButton.isEnabled = false
            accountButton.isEnabled = false
            helpButton.isEnabled = false

            progressBar.visibility = View.VISIBLE

            camera.captureImage(CameraKitView.ImageCallback { _, bytes ->
                Toast.makeText(activity, "Capture image", Toast.LENGTH_SHORT).show()
                var photo  = Base64.encodeToString(bytes, Base64.DEFAULT).replace("\n","")
                val apiService = ApiClient.instance
                apiService.recogniseCar(
                    CarPhoto(photo)
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ res ->
                        val car = (res as JsonArray)[0].asString
                        val probability = res[1].asDouble

                        if(probability >= 0.3) {
                            carNameView.text = car
                            Toast.makeText(activity, res.toString(), Toast.LENGTH_SHORT).show()

                            closeButton.visibility = View.VISIBLE

                            cardView.visibility = View.VISIBLE
                            val animFadein = AnimationUtils.loadAnimation(
                                activity?.applicationContext,
                                R.anim.fade_in
                            )
                            cardView.startAnimation(animFadein)

                            val colorFrom = Color.parseColor("#00000000")
                            val colorTo = Color.parseColor("#99000000")
                            val colorAnimation =
                                ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
                            colorAnimation.duration = 1000 // milliseconds

                            colorAnimation.addUpdateListener { animator ->
                                cameraRoot.background = ColorDrawable(animator.animatedValue as Int)
                                cameraRoot.foreground = ColorDrawable(animator.animatedValue as Int)
                            }
                            colorAnimation.start()

                            takePhotoButton.isEnabled = true
                            accountButton.isEnabled = true
                            helpButton.isEnabled = true
                        } else {
                            Toast.makeText(activity, "Не удалось распознать автомобиль, попробуйте ещё раз", Toast.LENGTH_SHORT).show()

                            takePhotoButton.isEnabled = true
                            accountButton.isEnabled = true
                            helpButton.isEnabled = true
                        }
                        progressBar.visibility = View.INVISIBLE
                    }, { error ->

                        takePhotoButton.isEnabled = true
                        accountButton.isEnabled = true
                        helpButton.isEnabled = true

                        progressBar.visibility = View.INVISIBLE

                        Toast.makeText(activity?.applicationContext, error.message, Toast.LENGTH_SHORT).show()
                    })

                val relativeLocation =
                    Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis().toString())
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val resolver = activity!!.contentResolver
                val uri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                try {

                    //uri?.let { uri ->
                    //    val stream = resolver.openOutputStream(uri)
                    //    stream?.write(bytes)
//
                    //    var mediaScanIntent = Intent(
                    //        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
                    //    );
                    //    mediaScanIntent.data = uri
                    //    this.sendBroadcast(mediaScanIntent)
                    //} ?: throw IOException("Failed to create new MediaStore record")
                } catch (e: IOException) {
                    if (uri != null) {
                        resolver.delete(uri, null, null)
                    }
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                }
            })
        }

        accountButton.setOnClickListener {
            
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    companion object {
        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
    }
}