package com.example.proyectociclo42.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.R
import com.example.proyectociclo42.model.ProductosCarrito
import com.squareup.picasso.Picasso

class AdaptaCarrito (private val dataSet: MutableList<ProductosCarrito>): RecyclerView.Adapter<AdaptaCarrito.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var carEntity = dataSet[position]

        holder.titleItems.text = carEntity.title;

        var total = carEntity.cost.toInt() * carEntity.amount.toInt()
        holder.costItems.text = carEntity.cost +" x "+ carEntity.amount + " = " + total

        Picasso.get().load(carEntity.imagen).into(holder.imagenItem);

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItems : TextView = itemView.findViewById<TextView>(R.id.titleItem)
        val costItems :TextView = itemView.findViewById<TextView>(R.id.costItem)
        val imagenItem : ImageView = itemView.findViewById<ImageView>(R.id.imagenItem)

}


}