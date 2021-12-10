package com.example.proyectociclo42

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.Adaptador.AdaptaCarrito
import com.example.proyectociclo42.model.ProductosCarrito
import com.google.firebase.firestore.FirebaseFirestore

class StorageActivity : AppCompatActivity(), AdaptaCarrito.OnItemClickListener {


    private lateinit var recycleView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var listCar = mutableListOf<ProductosCarrito>()
    private lateinit var carAdapter: AdaptaCarrito
    private var total = 0
    private var totalText : TextView?=null
    var idCar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        val prefs = getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        idCar = prefs.getString("car", null).toString()

        //Recycler
        recycleView = findViewById<RecyclerView>(R.id.reclyclerCar)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)

        getAllCarNew()
        carAdapter = AdaptaCarrito(listCar, this);
        recycleView.adapter = carAdapter;

        totalText = findViewById<TextView>(R.id.total)

        var buttonBuy = findViewById<Button>(R.id.buy)
        buttonBuy.setOnClickListener {

            db.collection("car").document(idCar).update("state",true)

            val prefsDeleteCar= getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefsDeleteCar.remove("car")
            prefsDeleteCar.apply()


            onOrder()


        }

    }

    private fun getAllCarNew() {
        listCar.clear()



        if(idCar != null && idCar.isNotEmpty() && idCar != "null"){

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

    override fun onItemClick(position: Int) {
        val carItem: ProductosCarrito = listCar[position]

        if(idCar != null && idCar.isNotEmpty() && idCar != "null"){
            db.collection("car").document(idCar).collection("products").document(carItem.id).delete()
        }

        getAllCarNew();
    }


    fun onOrder(){
        val orderIntent= Intent(this,OrderActivity::class.java)
        startActivity(orderIntent)
    }

    fun onOrderView(view: android.view.View) {

        onOrder();
    }







}