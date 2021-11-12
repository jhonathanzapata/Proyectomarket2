package com.example.proyectociclo42

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectociclo42.Adaptador.AdaptaProducto
import com.example.proyectociclo42.model.Productos


enum class ProviderType{
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {

    private var email_Text_View: TextView?=null
    private var provider_Text_View: TextView?=null

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
        val listaProductos: MutableList<Productos> = mutableListOf()

        val adapterProductos = AdaptaProducto(this, listaProductos)
        recyclerView_productos.adapter=adapterProductos

        val produto1 = Productos(
            R.drawable.cebollaproduc,
            "Cebolla cabezona",
            "Cebolla cabezona, cultivada de forma organica x libra",
            "1000"
        )

        listaProductos.add(produto1)

        val produto2 = Productos(
            R.drawable.cilantroproduc,
            "Cilantro",
            "Cilantro verde x racimo",
            "200"
        )

        listaProductos.add(produto2)

        val produto3 = Productos(
            R.drawable.peraproduc,
            "Pera",
            "Pera dulce importada x libra",
            "2000"
        )

        listaProductos.add(produto3)

        val produto4 = Productos(
            R.drawable.papaproduc,
            "Papa",
            "Papa sabanera,no lavada x100",
            "1500"
        )

        listaProductos.add(produto4)



        setup(email?:"",provider?:"")

        val prefs:SharedPreferences.Editor=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider",provider)
        prefs.apply()
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
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }
}