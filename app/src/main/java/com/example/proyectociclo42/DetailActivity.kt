package com.example.proyectociclo42

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

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

    private var costProduct: TextView? = null
    private var amountText: TextView? = null
    private var costUnit = 0
    private var image = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        textproduct = findViewById<TextView>(R.id.textProduct)
        imageproduct = findViewById<ImageView>(R.id.imageProduct)
        desProduct = findViewById<TextView>(R.id.desProduct)
        catProduct = findViewById<TextView>(R.id.catProducto)
        //preProduct = findViewById<TextView>(R.id.preProducto)
        idProduct = findViewById<TextView>(R.id.idProduct)
        listViewComments = findViewById<ListView>(R.id.listComments)
        costProduct = findViewById<TextView>(R.id.costProduct)
        amountText = findViewById<TextView>(R.id.amount)

        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        val product:String?=bundle?.getString("product")

        if (product != null) {
            loadProduct(product)
        }

        var buttonLess = findViewById<Button>(R.id.less)
        buttonLess.setOnClickListener {

            var amount =  amountText!!.text.toString().toInt()

            if(amount > 1){
                amount -= 1
                amountText!!.text = amount.toString()
            }

            costProduct!!.text = (costUnit * amount).toString()

        }

        var buttonMore = findViewById<Button>(R.id.more)
        buttonMore.setOnClickListener {

            var amount =  amountText!!.text.toString().toInt()

            if(amount < 10){
                amount += 1
                amountText!!.text = amount.toString()
            }

            costProduct!!.text = (costUnit * amount).toString()

        }

        var buttonAddCar = findViewById<ImageButton>(R.id.addCar)
        buttonAddCar.setOnClickListener {

            //Save Car
            var idCar = ""
            val getPrefs = getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE)
            idCar = getPrefs.getString("car", null).toString()
            var email = getPrefs.getString("email", null).toString()

            if(idCar == null || idCar.isEmpty() || idCar == "null"){
                idCar = UUID.randomUUID().toString()
                val setPrefs = getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                setPrefs.putString("car",idCar)
                setPrefs.apply()
            }

            db.collection("car").document(idCar).set(hashMapOf("state" to false,"email" to email))

            db.collection("car").document(idCar)
                .collection("products").document(idProduct!!.text.toString()).set(
                    hashMapOf("amount" to amountText!!.text.toString(),
                        "cost" to costUnit!!.toString(),
                        "image" to image,
                        "title" to textproduct!!.text.toString(),
                    ))


            val StorageIntent= Intent(this,StorageActivity::class.java)
            startActivity(StorageIntent)

        }


    }

    private fun loadProduct(product: String) {

        db.collection("product").document(product).get()
            .addOnSuccessListener {
                textproduct!!.setText(it.get("nombre") as String)
                Picasso.get().load(it.get("foto").toString()).into(imageproduct!!)
                image = it.get("foto").toString()
                idProduct!!.setText(it.id)
                desProduct!!.setText(it.get("descripcion") as String)
                catProduct!!.setText(it.get("categoria") as String)

                costProduct!!.text =  it.get("precio").toString()
                costUnit = it.get("precio").toString().toInt()

                //preProduct!!.setText(it.get("precio") as String)

            }

        db.collection("product").document(product).collection("comentarios").get()
            .addOnSuccessListener {

                if (it.any()) {

                    for (comment in it) {
                        listComments.add(
                            comment.get("usuario").toString() + " : "+"calificacion: "+comment.get("puntuacion").toString()+" ." + comment.get("comentario").toString()
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

        val prefs: SharedPreferences =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email:String?=prefs.getString("email",null)
        val provider:String?=prefs.getString("provider",null)

        if(email!=null && provider!=null) {
            ReturnHome(email,ProviderType.valueOf(provider))
        }
    }

    fun ReturnHome(email:String,provider:ProviderType){
        val returnHomeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(returnHomeIntent)
    }


}