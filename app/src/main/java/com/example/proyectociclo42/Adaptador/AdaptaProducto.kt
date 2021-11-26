package com.example.proyectociclo42.Adaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.R
import com.example.proyectociclo42.model.Productos
import com.squareup.picasso.Picasso



class AdaptaProducto (private val context: Context, private val productos:MutableList<Productos>,private val listener: OnItemClickListener): RecyclerView.Adapter<AdaptaProducto.ProductoViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val itemLista= LayoutInflater.from(context).inflate(R.layout.hardware_item,parent, false)
        val holder= ProductoViewHolder(itemLista)
        return holder
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {

//        holder.foto.setImageResource(productos[position].foto)
        Picasso.get().load(productos[position].foto).into(holder.foto)

        holder.nombre.text= productos[position].nombre
        holder.descripcion.text= productos[position].descripcion
        holder.precio.text= productos[position].precio
        holder.vendedor.text=productos[position].vendedor
        holder.categoria.text=productos[position].categoria
        holder.puntuacion.text=(productos[position].puntuacion).toString()


    }

    override fun getItemCount(): Int = productos.size


inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    val foto = itemView.findViewById<ImageView>(R.id.fotoProduto)
    val nombre = itemView.findViewById<TextView>(R.id.nombreProducto)
    val descripcion = itemView.findViewById<TextView>(R.id.descripcionProducto)
    val precio = itemView.findViewById<TextView>(R.id.precioProducto)
    val vendedor = itemView.findViewById<TextView>(R.id.vendedorProducto)
    val categoria = itemView.findViewById<TextView>(R.id.categoriaProducto)
    val puntuacion=itemView.findViewById<TextView>(R.id.promediopuntuacion)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(adapterPosition != RecyclerView.NO_POSITION){
            listener.onItemClick(adapterPosition)
        }
    }
}

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }



}