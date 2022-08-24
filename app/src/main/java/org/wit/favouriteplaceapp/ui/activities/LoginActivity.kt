package org.wit.favouriteplaceapp.ui.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activty_login.*
import org.wit.favouriteplaceapp.databinding.ActivtyLoginBinding
import org.wit.favouriteplaceapp.firestore.FirestoreClass
import org.wit.favouriteplaceapp.models.User

class LoginActivity : AppCompatActivity() {
    private lateinit var loginLayout: ActivtyLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginLayout = ActivtyLoginBinding.inflate(layoutInflater)
        setContentView(loginLayout.root)



        register_btn.setOnClickListener {

            //creating a intent to bring us from the login screen to the register screen
            val intent = Intent(this@LoginActivity, RegisterActivty::class.java)
            startActivity(intent)
        }
        login_button.setOnClickListener{
            logInRegisteredUser()
        }
    }


    private fun validateLogin(): Boolean {
        when {
            //Validation
            loginLayout.email.text.isEmpty() -> {
                Toast.makeText(this, "Please enter a email!", Toast.LENGTH_SHORT).show()
                return false
            }
            loginLayout.password.text.isEmpty() -> {
                Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun logInRegisteredUser() {
        if(validateLogin()) {

            val email = loginLayout.email.text.toString().trim{ it <= ' '}
            val password = loginLayout.password.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{task ->

                    if(task.isSuccessful){
                        FirestoreClass().getUserDetails(this@LoginActivity)

                    }
                    else {
                        Toast.makeText(this, "Unsuccessful login." + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    fun userLoggedInSuccessful(user: User){
        //Logging the firstname, lastname and the email...
        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

        Toast.makeText(this, "You are logged in successfully", Toast.LENGTH_SHORT).show()
        //we then redirect the user to the home page and close login activity...
        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        finish()
    }

}