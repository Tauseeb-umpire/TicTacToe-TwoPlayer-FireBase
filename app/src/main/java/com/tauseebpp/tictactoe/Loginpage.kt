package com.tauseebpp.tictactoe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_loginpage.*


class Loginpage : AppCompatActivity() {
    //private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mAuth: FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
        //mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth=FirebaseAuth.getInstance()
    }

    fun buclick(view: View) {
        val email=etEmail.text.toString()
        val password=etPassword.text.toString()
        loginwithnewaccount(email, password)
    }

    fun loginwithnewaccount(email: String, password: String){
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        applicationContext,
                        "You have Sucessfully Login",
                        Toast.LENGTH_LONG
                    ).show()
                    val user = mAuth!!.currentUser
                    if(user!=null) {
                        myRef.child("Users").child(SplitString(user.email.toString())).child("Request").setValue(user.uid)
                    }
                    Log.d("Login", user!!.uid)
                    loadMainactivity()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        applicationContext,
                        "You entered Invalid data or Check Your Connectivity",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // ...
            }
    }

    override fun onStart() {
        super.onStart()
        loadMainactivity()
    }

    fun loadMainactivity(){
        var currentuser=mAuth!!.currentUser
        if(currentuser!=null){
            //save in data base
            var intent=Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentuser.email)
            intent.putExtra("uid", currentuser.uid)
            startActivity(intent)
        }
    }

    fun SplitString(str:String):String{
        var split=str.split("@")
        return split[0]
    }
}