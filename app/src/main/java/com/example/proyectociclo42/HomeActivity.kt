package com.example.proyectociclo42

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.Adaptador.AdaptaProducto
import com.example.proyectociclo42.model.Productos
import com.google.firebase.firestore.FirebaseFirestore






enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    AdapterView.OnItemSelectedListener, AdaptaProducto.OnItemClickListener{

    private var email_Text_View: TextView?=null
    private var provider_Text_View: TextView?=null

    var db=FirebaseFirestore.getInstance()

    private var listProduct = mutableListOf<Productos>()
    private lateinit var productAdapter: AdaptaProducto
    private lateinit var recycleView: RecyclerView

    private lateinit var svSearch: SearchView

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSeller: Spinner
    private var listCategory = arrayListOf<String>()
    private var listSeller = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle:Bundle?=intent.extras
        val email:String?=bundle?.getString("email")
        val provider:String?=bundle?.getString("provider")
        val recyclerView_productos = findViewById<RecyclerView>(R.id.recyclerView_productos)
        recyclerView_productos.layoutManager = LinearLayoutManager(this)
        recyclerView_productos.setHasFixedSize(true)

        //configuracion del adaptador de la lista
//        val listaProductos: MutableList<Productos> = mutableListOf()

        //Adición de productos por Firestore
        getAllProduct()

        productAdapter = AdaptaProducto(this, listProduct,this)
        recyclerView_productos.adapter=productAdapter

        //Search
        svSearch = findViewById<SearchView>(R.id.svSearch)
        svSearch.setOnQueryTextListener(this)

        //Spinner Category
        spinnerCategory = findViewById(R.id.category_spinner)
        spinnerCategory.onItemSelectedListener = this
        listCategory.add("All")

        this?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                listCategory
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
        }

        //Spinner Seller
        spinnerSeller = findViewById(R.id.seller_spinner)
        spinnerSeller.onItemSelectedListener = this
        listSeller.add("All")

        this?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                listSeller
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSeller.adapter = adapter
            }
        }

/*

//Adición manual de productos

        val produto1 = Productos(
            R.drawable.cebollaproduc,
            "Cebolla cabezona",
            "Cebolla cabezona, cultivada de forma organica x libra",
            "1000",
            "Frutas",
            "Carlos"

        )

        listaProductos.add(produto1)

        val produto2 = Productos(
            R.drawable.cilantroproduc,
            "Cilantro",
            "Cilantro verde x racimo",
            "200",
            "Frutas",
            "Diana"
        )

        listaProductos.add(produto2)

        val produto3 = Productos(
            R.drawable.peraproduc,
            "Pera",
            "Pera dulce importada x libra",
            "2000",
            "Frutas",
            "Carlos"
        )

        listaProductos.add(produto3)

        val produto4 = Productos(
            R.drawable.papaproduc,
            "Papa",
            "Papa sabanera,no lavada x100",
            "1500",
            "Hortalizas",
            "Diana"
        )

        listaProductos.add(produto4)*/



        setup(email?:"",provider?:"")

        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()

    }

    private fun getAllProduct(){

        listProduct.clear()

        db.collection("product").get().addOnSuccessListener { result ->


            for (document in result){

                if (!listCategory.contains(document.data["categoria"].toString())) {
                    listCategory.add(document.data["categoria"].toString())
                }

                if (!listSeller.contains(document.data["vendedor"].toString())) {
                    listSeller.add(document.data["vendedor"].toString())
                }

                var productExist = listProduct.find { it.id == document.id }

                if (productExist == null) {


                    listProduct.add(
                        Productos(
//                        document.data["foto"].toString(),
//                        document.data["nombre"].toString(),
//                        document.data["descripcion"].toString(),
//                        document.data["precio"].toString(),
//                        document.data["vendedor"].toString(),
//                        document.data["categoria"].toString()

                            document.get("foto").toString(),
                            document.get("nombre").toString(),
                            document.get("descripcion").toString(),
                            document.get("precio").toString(),
                            document.get("vendedor").toString(),
                            document.get("categoria").toString(),
                            document.id,
                            document.get("promedio").toString().toDouble(),

                            )
                    )
                }
            }

            averageScore();

            productAdapter.notifyDataSetChanged();
        }

    }

    private fun setup(email:String,provider:String){

        title="Inicio"

        email_Text_View=findViewById(R.id.textviewemail)
        provider_Text_View=findViewById(R.id.textviewprovider)

        email_Text_View?.text=email
        provider_Text_View?.text=provider

    }

    fun signOut(botonCerrarSesion:View){

        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.remove("email")
        prefs.remove("provider")
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        //onBackPressed()
        val intentsignout= Intent(this, AuthActivity::class.java)
        startActivity(intentsignout)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText!!.isEmpty()) {
            getAllProduct()
        } else {
            searchForTitle(newText)
        }

        return true;
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {

            var category: String = spinnerCategory.getSelectedItem().toString()
            var seller: String = spinnerSeller.getSelectedItem().toString()

            var all = "All"

            if (category == all && seller == all) {
                getAllProduct()
            } else {
                if (category == all) {
                    filterForSeller(seller)
                } else {
                    if (seller == all) {
                        filterForCategory(category)
                    } else {
                        filterForCategoryAndSeller(category, seller)
                    }
                }
            }

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun filterForCategory(category: String) {
        listProduct.clear()

        db.collection("product")
            .whereEqualTo("categoria", category)
            .get().addOnSuccessListener { result ->
                for (document in result) {

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {


                        listProduct.add(
                            Productos(
                                //
                                document.get("foto").toString(),
                                document.get("nombre").toString(),
                                document.get("descripcion").toString(),
                                document.get("precio").toString(),
                                document.get("vendedor").toString(),
                                document.get("categoria").toString(),
                                document.id,
                                document.get("promedio").toString().toDouble(),
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    private fun filterForSeller(seller: String) {
        listProduct.clear()

        db.collection("product")
            .whereEqualTo("vendedor", seller)
            .get().addOnSuccessListener { result ->
                for (document in result) {

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {

                        listProduct.add(
                            Productos(
                                //
                                document.get("foto").toString(),
                                document.get("nombre").toString(),
                                document.get("descripcion").toString(),
                                document.get("precio").toString(),
                                document.get("vendedor").toString(),
                                document.get("categoria").toString(),
                                document.id,
                                document.get("promedio").toString().toDouble(),
                            )
                        )
                    }

                }
                productAdapter.notifyDataSetChanged();
            }
    }

    private fun filterForCategoryAndSeller(category: String, seller: String) {
        listProduct.clear()

        db.collection("product")
            .whereEqualTo("vendedor", seller).whereEqualTo("categoria", category)
            .get().addOnSuccessListener { result ->
                for (document in result) {

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {

                        listProduct.add(
                            Productos(
                                //
                                document.get("foto").toString(),
                                document.get("nombre").toString(),
                                document.get("descripcion").toString(),
                                document.get("precio").toString(),
                                document.get("vendedor").toString(),
                                document.get("categoria").toString(),
                                document.id,
                                document.get("promedio").toString().toDouble(),
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }



    private fun searchForTitle(newText: String) {
        listProduct.clear()
        db.collection("product")
            .whereGreaterThanOrEqualTo("nombre", newText)
            .whereLessThanOrEqualTo("nombre", (newText + "\uF7FF"))
            .get().addOnSuccessListener { result ->
                for (document in result) {

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {

                        listProduct.add(
                            Productos(
//
                                document.get("foto").toString(),
                                document.get("nombre").toString(),
                                document.get("descripcion").toString(),
                                document.get("precio").toString(),
                                document.get("vendedor").toString(),
                                document.get("categoria").toString(),
                                document.id,
                                document.get("promedio").toString().toDouble(),
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    override fun onItemClick(position: Int) {
        val productItem: Productos = listProduct[position]

        //Go  ProductActivity

        val DetailIntent= Intent(this,DetailActivity::class.java).apply {

            putExtra("product",productItem.id)
        }
        startActivity(DetailIntent)

    }

    private fun averageScore(){

        for (product in listProduct) {
            var average = 0.0

            db.collection("product").document(product.id).collection("comentarios").get()
                .addOnSuccessListener {

                    if (it.any()) {
                        for (score in it) {
                            average += (score.get("puntuacion").toString()).toDouble()

                        }
                        db.collection("product").document(product.id).update(
                            "promedio",  average / it.count()
                        )
                    }
                }


        }


    }
}