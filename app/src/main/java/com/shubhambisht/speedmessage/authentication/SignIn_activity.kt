package com.shubhambisht.speedmessage.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.shubhambisht.speedmessage.R
import com.shubhambisht.speedmessage.messages.LatestMessage
import com.shubhambisht.speedmessage.messages.NewMessageActivity
import kotlinx.android.synthetic.main.activity_signin.*

class SignIn_activity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        back.setOnClickListener { finish() }
        signin.setOnClickListener { PerformLogin() }
    }
private fun PerformLogin(){
    if (username_signin.toString().isEmpty() || password_Signin.toString().isEmpty()) {
        return
    }else
    FirebaseAuth.getInstance().signInWithEmailAndPassword(username_signin.text.toString(), password_Signin.text.toString())
            .addOnCompleteListener {
              val intent = Intent(applicationContext,NewMessageActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Log.d("signin ",it.localizedMessage.toString())
            } }


}
