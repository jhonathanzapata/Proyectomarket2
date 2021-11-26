package com.example.proyectociclo42

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()

    private var textproduct: TextView? = null
    private var imageproduct: ImageView? = null
    private var idProduct: TextView? = null
    private var desProduct: TextView? = null
    private var catProduct: TextView? = null
    private var preProduct: TextView? = null
    private var listViewComments: ListView? = null
    private var listComments = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        textproduct = findViewById<TextView>(R.id.textProduct)
        imageproduct = findViewById<ImageView>(R.id.imageProduct)
        desProduct = findViewById<TextView>(R.id.desProduct)
        catProduct = findViewById<TextView>(R.id.catProducto)
        preProduct = findViewById<TextView>(R.id.preProducto)
        idProduct = findViewById<TextView>(R.id.idProduct)
        listViewComments = findViewById<ListView>(R.id.listComments)

        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        val product:String?=bundle?.getString("product")

        if (product != null) {
            loadProduct(product)
        }
    }

    private fun loadProduct(product: String) {

        db.collection("product").document(product).get()
            .addOnSuccessListener {
                textproduct!!.setText(it.get("nombre") as String)
                Picasso.get().load(it.get("foto").toString()).into(imageproduct!!)
                idProduct!!.setText(it.id)
                desProduct!!.setText(it.get("descripcion") as String)
                catProduct!!.setText(it.get("categoria") as String)
                preProduct!!.setText(it.get("precio") as String)

            }

        db.collection("product").document(product).collection("comentarios").get()
            .addOnSuccessListener {

                if (it.any()) {

                    for (comment in it) {
                        listComments.add(
                            comment.get("usuario").toString() + " : " + comment.get("comentario").toString()
                        )
                    }

                    listViewComments!!.adapter = this?.let { it1 ->
                        ArrayAdapter(
                            it1,
                            android.R.layout.simple_dropdown_item_1line,
                            listComments
                        )
                    }

                }
            }
    }

    fun onReturnHome(view: android.view.View) {
        val returnHomeIntent = Intent(this, HomeActivity::class.java)
        startActivity(returnHomeIntent)
    }


}