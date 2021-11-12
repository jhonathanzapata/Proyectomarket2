package com.example.proyectociclo42

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

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