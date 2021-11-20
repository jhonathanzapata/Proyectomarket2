package com.example.proyectociclo42

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private var editUserName: EditText? = null
    private var editPassword: EditText? = null
    private var editName: EditText? = null
    private var editLastName: EditText? = null
    private var editMobile: EditText? = null
    private var stTerms: Switch? = null

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editUserName = findViewById(R.id.edt_UserName);
        editPassword = findViewById(R.id.edt_PasswordRegister);
        editName = findViewById(R.id.edt_Name);
        editLastName = findViewById(R.id.edt_LastName);
        editMobile = findViewById(R.id.edt_Mobile);
        stTerms = findViewById(R.id.swt_Terms);
    }

    fun onRegister(view: android.view.View) {

        var username = editUserName!!.text.toString();
        var password = editPassword!!.text.toString();
        var name = editName!!.text.toString();
        var lastname = editLastName!!.text.toString();
        var mobile = editMobile!!.text.toString();
        var terms = stTerms!!.isChecked;

        if (Validation(username, password,name,lastname,mobile,terms)) {

            //Save Auth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Save User
                        db.collection("user").document(username).set(
                            hashMapOf("password" to password,
                                "name" to name,
                                "lastname" to lastname,
                                "mobile" to mobile,
                                "terms" to terms))

                        showHome(username, ProviderType.BASIC)
                    } else {
                        showAlertAuth()
                    }
                }

        }else{
            showAlertValidation()
        }

    }

    private fun Validation(username: String, password: String,name: String,lastname: String,mobile: String,terms: Boolean): Boolean {

        //Reset
        editUserName!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editUserNameLayout = findViewById<TextInputLayout>( R.id.edt_UserNameLayout)
        editUserNameLayout!!.setHint("usuario@gmail.com")

        editPassword!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editPasswordLayout = findViewById<TextInputLayout>( R.id.edt_PasswordRegisterLayout)
        editPasswordLayout!!.setHint("Your Password")

        editName!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editNameLayout = findViewById<TextInputLayout>( R.id.edt_NameLayout)
        editNameLayout!!.setHint("Your Name")

        editLastName!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editLastNameLayout = findViewById<TextInputLayout>( R.id.edt_LastNameLayout)
        editLastNameLayout!!.setHint("Your Last Name")

        editMobile!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editMobileLayout = findViewById<TextInputLayout>( R.id.edt_MobileLayout)
        editMobileLayout!!.setHint("3111111111")

        stTerms!!.setBackground(resources.getDrawable(R.drawable.customborderok))

        //Regex
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val lowercase: Pattern = Pattern.compile("[a-z]")
        val digit: Pattern = Pattern.compile("[0-9]")
        val character: Pattern = Pattern.compile("[!#\$%&'*+/=?^_`{|}~-]")
        val email: Pattern = Pattern.compile("^[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\$")

        //Validation
        var validation : Boolean = true;

        if (name.isEmpty()) {
            editNameLayout!!.setHint("Empty email")
            editName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (lastname.isEmpty()) {
            editLastNameLayout!!.setHint("Empty Last Name")
            editLastName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (mobile.isEmpty()) {
            editMobileLayout!!.setHint("Empty Mobile")
            editMobile!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (!terms){
            stTerms!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        //Validate Password
        if (password.isEmpty()) {
            editPasswordLayout!!.setHint("Empty Password")
            editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false

        }else{
            if (password.length < 8) {
                editPasswordLayout!!.setHint("Minimum 8 characters")
                editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                validation=false

            }else{
                if (!lowercase.matcher(password).find()) {
                    editPasswordLayout!!.setHint("At least one lowercase")
                    editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                    validation=false

                }else{
                    if (!uppercase.matcher(password).find()) {
                        editPasswordLayout!!.setHint("At least one upercase")
                        editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                        validation=false
                    }else{

                        if (!digit.matcher(password).find()){
                            editPasswordLayout!!.setHint("At least one digit")
                            editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                            validation=false

                        }else{
                            if (!character.matcher(password).find()){
                                editPasswordLayout!!.setHint("At least one special character")
                                editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                                validation=false
                            }
                        }
                    }
                }
            }
        }

        //Validation Email
        if (username.isEmpty()) {
            editUserNameLayout!!.setHint("Empty email")
            editUserName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }else{
            if (!email.matcher(username).find()){
                editUserNameLayout!!.setHint("It is not an email")
                editUserName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                validation=false
            }
        }

        return validation;
    }

    private fun showAlertValidation(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en la validación")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun showAlertAuth(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun showHome(email:String,provider:ProviderType){
        val HomeIntent= Intent(this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(HomeIntent)
    }

    fun onTerms(view: android.view.View) {

        AlertDialog.Builder(this)
            .setTitle("Terminos y condiciones")
            .setMessage("Usted acepta todos los terinos y condiciones")
            .setPositiveButton("Aceptar",positiveButton)
            .setNegativeButton("Cancelar",negativeButton)
            .create().show();
    }

    val positiveButton={ _: DialogInterface, _:Int->
        stTerms!!.setChecked(true);
    }

    val negativeButton={ _: DialogInterface, _:Int->
        stTerms!!.setChecked(false);
    }

    fun onReturnLogin(view: android.view.View) {
        val loginIntent = Intent(this, AuthActivity::class.java)
        startActivity(loginIntent)
    }


}