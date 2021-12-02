package com.example.proyectociclo42

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.Adaptador.AdaptaCarrito
import com.example.proyectociclo42.model.ProductosCarrito
import com.google.firebase.firestore.FirebaseFirestore

class StorageActivity : AppCompatActivity() {


    private lateinit var recycleView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var listCar = mutableListOf<ProductosCarrito>()
    private lateinit var carAdapter: AdaptaCarrito
    private var total = 0
    private var totalText : TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        //Recycler
        recycleView = findViewById<RecyclerView>(R.id.reclyclerCar)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)

        getAllCarNew()
        carAdapter = AdaptaCarrito(listCar);
        recycleView.adapter = carAdapter;

        totalText = findViewById<TextView>(R.id.total)
    }

    private fun getAllCarNew() {
        listCar.clear()

        var idCar = ""
        val prefs = getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        idCar = prefs.getString("car", null).toString()

        if(idCar != null && idCar.isNotEmpty()){

            db.collection("car").document(idCar).collection("products")
                .get().addOnSuccessListener {
                    if (it.any()) {
                        for (item in it) {
                            listCar.add(
                                ProductosCarrito(
                                    item.data["image"].toString(),
                                    item.data["title"].toString(),
                                    item.data["cost"].toString(),
                                    item.data["amount"].toString(),
                                    item.id.toString()
                                )
                            )

                            total += (item.data["cost"].toString()
                                .toInt() * item.data["amount"].toString().toInt())

                            totalText!!.text = total.toString()
                        }
                        carAdapter.notifyDataSetChanged();
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