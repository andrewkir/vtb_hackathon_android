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
import android.preference.PreferenceManager
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
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_main.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.CarDetails
import ru.andrewkir.vtbmobile.DataClasses.CarPhoto
import java.io.IOException

class CameraFragment(var func: (position: Int) -> Unit) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        var carResult: CarDetails? = null

        cardView.visibility = View.INVISIBLE
        closeButton.visibility = View.INVISIBLE


        createLoan.setOnClickListener {
            var intent = Intent(activity, CreditActivity::class.java)
            intent.putExtra("maxPrice", carResult!!.maxPrice)
            intent.putExtra("carName", "${carResult!!.make} ${carResult!!.model}")
            intent.putExtra("priceRange", "${carResult!!.minPrice} - ${carResult!!.maxPrice}")
            val options =
                ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out)
            this.startActivity(intent, options.toBundle())
            camera.onStop()
        }

        closeButton.setOnClickListener {
            takePhotoButton.isEnabled = true
            accountButton.isEnabled = true
            historyButton.isEnabled = true

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
            historyButton.isEnabled = false

            progressBar.visibility = View.VISIBLE

            camera.captureImage(CameraKitView.ImageCallback { _, bytes ->
                var photo = Base64.encodeToString(bytes, Base64.DEFAULT).replace("\n", "")
                val apiService = ApiClient(activity!!.applicationContext).instance

                var access = ""
                var refresh = ""

                val sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(activity)
                access = sharedPref.getString("access_token", "")!!

                if (access != "") {
                    apiService.recogniseCar(
                        "Bearer $access",
                        CarPhoto(photo)
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ res ->
                            val car = ((res as JsonObject).get("car") as JsonArray)[0].asString
                            val probability = ((res as JsonObject).get("car") as JsonArray)[1].asDouble

                            if (probability >= 0.3) {
                                carNameView.text = car

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
                                    cameraRoot.background =
                                        ColorDrawable(animator.animatedValue as Int)
                                    cameraRoot.foreground =
                                        ColorDrawable(animator.animatedValue as Int)
                                }
                                colorAnimation.start()

                                takePhotoButton.isEnabled = false
                                accountButton.isEnabled = false
                                historyButton.isEnabled = false

                                val body = JsonObject()
                                body.addProperty("query", car)

                                apiService.marketPlaceSearch(
                                    body
                                )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        carResult = result
                                        Picasso.get().load(result.imageUrl).into(carPhoto)
                                        carDescription.text =
                                            "Цены: ${result.minPrice} - ${result.maxPrice}\nРасход: ${result.specs.fuelConsumption}\nМаксимальная скорость: ${result.specs.speedLimit}\nРазгон до 100 км/ч: ${result.specs.acceleration}"
                                    }, { error ->
                                        progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT)
                                            .show()
                                    })
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Не удалось распознать автомобиль, попробуйте ещё раз",
                                    Toast.LENGTH_SHORT
                                ).show()

                                takePhotoButton.isEnabled = true
                                accountButton.isEnabled = true
                                historyButton.isEnabled = true
                            }
                            progressBar.visibility = View.INVISIBLE
                        }, { error ->

                            takePhotoButton.isEnabled = true
                            accountButton.isEnabled = true
                            historyButton.isEnabled = true

                            progressBar.visibility = View.INVISIBLE

                            Toast.makeText(
                                activity?.applicationContext,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                } else {
                    apiService.recogniseCar(
                        CarPhoto(photo)
                    )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ res ->
                            val car = (res as JsonArray)[0].asString
                            val probability = res[1].asDouble

                            if (probability >= 0.3) {
                                carNameView.text = car

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
                                    cameraRoot.background =
                                        ColorDrawable(animator.animatedValue as Int)
                                    cameraRoot.foreground =
                                        ColorDrawable(animator.animatedValue as Int)
                                }
                                colorAnimation.start()

                                takePhotoButton.isEnabled = false
                                accountButton.isEnabled = false
                                historyButton.isEnabled = false

                                val body = JsonObject()
                                body.addProperty("query", car)

                                apiService.marketPlaceSearch(
                                    body
                                )
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        carResult = result
                                        Picasso.get().load(result.imageUrl).into(carPhoto)
                                        carDescription.text =
                                            "Цены: ${result.minPrice} - ${result.maxPrice}\nРасход: ${result.specs.fuelConsumption}\nМаксимальная скорость: ${result.specs.speedLimit}\nРазгон до 100 км/ч: ${result.specs.acceleration}"
                                    }, { error ->
                                        progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT)
                                            .show()
                                    })
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Не удалось распознать автомобиль, попробуйте ещё раз",
                                    Toast.LENGTH_SHORT
                                ).show()

                                takePhotoButton.isEnabled = true
                                accountButton.isEnabled = true
                                historyButton.isEnabled = true
                            }
                            progressBar.visibility = View.INVISIBLE
                        }, { error ->

                            takePhotoButton.isEnabled = true
                            accountButton.isEnabled = true
                            historyButton.isEnabled = true

                            progressBar.visibility = View.INVISIBLE

                            Toast.makeText(
                                activity?.applicationContext,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                }
            })
        }

        accountButton.setOnClickListener {
            func(2)
        }
        historyButton.setOnClickListener {
            func(0)
        }
    }
}