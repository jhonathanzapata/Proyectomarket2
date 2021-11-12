package com.example.proyectociclo42

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


enum class ProviderType{
    BASIC
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
        recyclerView_productos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)


        setup(email?:"",provider?:"")
    }

    private fun setup(email:String,provider:String){

        title="Inicio"

        email_Text_View=findViewById(R.id.textviewemail)
        provider_Text_View=findViewById(R.id.textviewprovider)

        email_Text_View?.text=email
        provider_Text_View?.text=provider

    }

    fun signOut(botonCerrarSesion:View){
        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }
}