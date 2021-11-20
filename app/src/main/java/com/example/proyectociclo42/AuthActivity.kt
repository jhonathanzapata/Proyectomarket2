package com.example.proyectociclo42

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN=100

    private var edt_email: EditText?=null
    private var edt_password: EditText?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        session()
    }

    override fun onStart() {
        super.onStart()

        var authlayout=findViewById<LinearLayout>(R.id.authlayout)
        authlayout.visibility=View.VISIBLE
    }

    fun setupgoogle(botongoogle:View){

        val googleConf=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id2))
            .requestEmail()
            .build()

        val googleclient=GoogleSignIn.getClient(this,googleConf)

        googleclient.signOut()

        startActivityForResult(googleclient.signInIntent, GOOGLE_SIGN_IN)

    }



    private fun session(){

        val prefs: SharedPreferences=getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email:String?=prefs.getString("email",null)
        val provider:String?=prefs.getString("provider",null)

        if(email!=null && provider!=null){

            var authlayout=findViewById<LinearLayout>(R.id.authlayout)
            authlayout.visibility=View.INVISIBLE
            showHome(email,ProviderType.valueOf(provider))
        }

    }
    fun setup2(botonregister: View){
        val RegisterIntent= Intent(this,RegisterActivity::class.java)
        startActivity(RegisterIntent)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==GOOGLE_SIGN_IN){
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)

            try{
                val account= task.getResult(ApiException::class.java)

                if(account!=null){

                    val credential=GoogleAuthProvider.getCredential(account.idToken,null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if(it.isSuccessful){
                            showHome(account.email?:"",ProviderType.GOOGLE)
                        }else{
                            showAlert()
                        }
                    }
                }
            } catch (e:ApiException){
                showAlert()
            }



        }
    }
}