package com.example.proyectociclo42

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.Adaptador.OrderAdapter
import com.example.proyectociclo42.model.OrderEntity
import com.google.firebase.firestore.FirebaseFirestore

class OrderActivity : AppCompatActivity() {
    private lateinit var recycleView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var listOrder = mutableListOf<OrderEntity>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        recycleView = findViewById<RecyclerView>(R.id.reclyclerOrder)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.setHasFixedSize(true)

        getAllOrderNew()
        orderAdapter = OrderAdapter(listOrder);
        recycleView.adapter = orderAdapter;

    }
    private fun getAllOrderNew() {
        listOrder.clear()

        val prefs =getSharedPreferences(resources.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        var email = prefs.getString("email", null).toString()

        db.collection("car").whereEqualTo("state", true).whereEqualTo("email",email)
            .get().addOnSuccessListener {
                if (it.any()) {

                    var idOrder = 1;
                    for (item in it) {

                        var des = ""

                        db.collection("car").document(item.id).collection("products")
                            .get().addOnSuccessListener { products ->
                                if (products.any()) {
                                    for (product in products) {
                                        des = des + product.data["title"] + " x " + product.data["amount"] + "\n"
                                    }

                                    listOrder.add(
                                        OrderEntity(
                                             "Order"+" " + idOrder,
                                            des
                                        )
                                    )
                                    idOrder++
                                    orderAdapter.notifyDataSetChanged();
                                }
                            }

                    }
                    orderAdapter.notifyDataSetChanged();
                }
            }
    }




}