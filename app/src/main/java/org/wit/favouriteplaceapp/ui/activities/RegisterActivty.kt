package org.wit.favouriteplaceapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_register.*
import org.wit.favouriteplaceapp.databinding.ActivityRegisterBinding
import org.wit.favouriteplaceapp.firestore.FirestoreClass
import org.wit.favouriteplaceapp.models.User

class RegisterActivty : AppCompatActivity() {
    private lateinit var registerLayout: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLayout = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerLayout.root)

        reg_login.setOnClickListener {

            val intent = Intent(this@RegisterActivty, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        reg_button.setOnClickListener{
            registerUser()
        }
    }


      private fun validateRegistration(): Boolean {
            when {
                //Validation
                registerLayout.name.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a first name!", Toast.LENGTH_SHORT).show()
                    return false
                }
                registerLayout.lastname.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a last name!", Toast.LENGTH_SHORT).show()
                    return false
                }
                registerLayout.emailAdd.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a valid Email Address!", Toast.LENGTH_SHORT).show()
                    return false
                }
                registerLayout.password.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a password!", Toast.LENGTH_SHORT).show()
                    return false
                }
                registerLayout.confirmPassword.text.isEmpty() -> {
                    Toast.makeText(this, "Please enter a confirm password!", Toast.LENGTH_SHORT).show()
                    return false
                }
                registerLayout.password.text.toString() != registerLayout.confirmPassword.text.toString() -> {
                    Toast.makeText(this, "Passwords dont match!", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
          return true
      }


    private fun registerUser() {
        //Checking the validating function to make sure details are all correct
        if(validateRegistration()){
            //trim email and password...
            val email: String = email_add.text.toString().trim{ it <= ' '}
            val password: String = password.text.toString().trim{ it <= ' '}

            // Create an instance and create a register a user with email and password
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if(task.isSuccessful){
                            //Firebase registering user..
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user = User(
                                firebaseUser.uid,
                                name.text.toString().trim{ it <= ' '},
                                lastname.text.toString().trim{ it <= ' '},
                                email_add.text.toString().trim{ it <= ' '}
                            )
                            //we pass the function and use the user information to pass through the function..
                            FirestoreClass().registerTheUser(this@RegisterActivty, user)

                        }
                        else {
                            //If registration is unsuccessful toast message the error message..
                            Toast.makeText(this, "Unsuccessful registration." + task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                )


        }
    }
     fun userIsRegistered() {
        Toast.makeText(this@RegisterActivty, "Registration was Successful",Toast.LENGTH_SHORT).show()
         FirebaseAuth.getInstance().signOut()
         finish()

    }
}


