package com.example.proyectociclo42

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth




class AuthActivity : AppCompatActivity() {

    private var edt_email: EditText?=null
    private var edt_password: EditText?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

    }

    fun setup(botonregister:View){

        title="Autenticación"

        edt_email=findViewById(R.id.edt_email)
        edt_password=findViewById(R.id.edt_password)

        var email:String=edt_email!!.text.toString()
        var password:String=edt_password!!.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    showHome(it.result?.user?.email?:"",ProviderType.BASIC)

                }else{

                    showAlert()
                }
            }
        }


    }

    fun login(botonlogin:View){

        title="Autenticación"

        edt_email=findViewById(R.id.edt_email)
        edt_password=findViewById(R.id.edt_password)

        var email:String=edt_email!!.text.toString()
        var password:String=edt_password!!.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener{
                if(it.isSuccessful){
                    showHome(it.result?.user?.email?:"",ProviderType.BASIC)

                }else{

                    showAlert()
                }
            }
        }


    }


    private fun showAlert(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }

    private fun showHome(email:String,provider:ProviderType){
        val HomeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(HomeIntent)

    }
}