package ru.andrewkir.vtbmobile

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.andrewkir.vtbmobile.DataClasses.CarDetails
import ru.andrewkir.vtbmobile.DataClasses.CarPhoto

class RecyclerAdapter(private val cars: MutableList<CarDetails>) :
    RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(cars[position])
    }

    override fun getItemCount(): Int = cars.size
}
class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.raw_search_history, parent, false)) {
    private var carNameView: TextView? = null
    private var carPrice: TextView? = null
    private var carPhoto: ImageView? = null
    private var carDesc: TextView? = null
    init {
        carNameView = itemView.findViewById(R.id.carName)
        carPrice = itemView.findViewById(R.id.carPrice)
        carPhoto = itemView.findViewById(R.id.carPhoto)
        carDesc = itemView.findViewById(R.id.carDesc)
    }
    fun bind(car: CarDetails) {
        carNameView?.text = car.make + " " + car.model
        carPrice?.text = "${car.minPrice}"
        Picasso.get().load(car.imageUrl).into(carPhoto)
        carDesc?.text = "Цены: ${car.minPrice} - ${car.maxPrice}\nРасход: ${car.specs.fuelConsumption}\nМаксимальная скорость: ${car.specs.speedLimit}\nРазгон до 100 км/ч: ${car.specs.acceleration}"
    }
}
