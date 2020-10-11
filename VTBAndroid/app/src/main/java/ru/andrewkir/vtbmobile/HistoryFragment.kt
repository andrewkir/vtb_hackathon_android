package ru.andrewkir.vtbmobile

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history.*
import ru.andrewkir.vtbmobile.Api.ApiClient
import ru.andrewkir.vtbmobile.DataClasses.CarDetails

class HistoryFragment(var func: (position: Int) -> Unit) : Fragment() {
    var carList = mutableListOf<CarDetails>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeActivity.setOnClickListener {
            func(1)
        }
        searchHistory.layoutManager = LinearLayoutManager(activity)

        swipe_container.setOnRefreshListener {
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)
            var access = sharedPref.getString("access_token", "")!!
            val apiService = ApiClient(activity!!.applicationContext).instance
            apiService.searchHistory(
                "Bearer $access"
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ res ->
                    val size = (res as JsonArray).size()
                    for (i in 0 until size) {
                        var resp = ((res as JsonArray)[i] as JsonObject).get("response").toString()

                        resp = resp.removeRange(0..13)
                        resp = resp.split(",")[0]
                        resp = resp.replace("\"", "")
                        resp = resp.replace("\\", "")

                        val body = JsonObject()
                        body.addProperty("query", resp)

                        apiService.marketPlaceSearch(
                            body
                        )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                carList.add(result)
                            }, { error ->
                                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                                swipe_container.isRefreshing = false
                            })
                    }
                    val adapter = RecyclerAdapter(carList)
                    searchHistory.adapter = adapter
                    adapter.notifyDataSetChanged()

                    swipe_container.isRefreshing = false
                }, { error ->
                    Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                    swipe_container.isRefreshing = false
                })
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            carList.clear()
        }
    }
}