package com.shubhambisht.speedmessage.authentication
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shubhambisht.speedmessage.messages.NewMessageActivity
import com.shubhambisht.speedmessage.R
import com.shubhambisht.speedmessage.models.Users
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // NEW USER
        register_login.setOnClickListener {
            PerformRegister()
        }
        //  OLD USER
        acc_already_login.setOnClickListener {
                val intent = Intent(applicationContext, SignIn_activity::class.java)
                startActivity(intent)
            }
        upload_pic.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }
private fun PerformRegister(){
    val username = username_login.text.toString()
    val email = email_login.text.toString()
    val password = password_login.text.toString()
    val mAuth = FirebaseAuth.getInstance()
    if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
        return
    }
    else {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (!it.isSuccessful) return@addOnCompleteListener
            else { Toast.makeText(this, "user created", Toast.LENGTH_SHORT).show()
                ImagetoStorage()
            }
        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage.toString(),Toast.LENGTH_LONG).show()
        } } }
    var selecteduri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 || resultCode == Activity.RESULT_OK || data != null) {
            selecteduri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selecteduri)
           selectphoto_imageview_register.setImageBitmap(bitmap)
            upload_pic.alpha = 0f
            // val bitmapDrawable = BitmapDrawable(bitmap)
           // upload_pic.setBackgroundDrawable(bitmapDrawable)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun ImagetoStorage() {
        if (selecteduri == null){ return}
        val filename = UUID.randomUUID().toString()
        val myRef = FirebaseStorage.getInstance().getReference("/images/$filename")
        myRef.putFile(selecteduri!!).addOnSuccessListener {
              Toast.makeText(this, "successfully uploaded", Toast.LENGTH_SHORT).show()
            /**  val uuid = UUID.randomUUID().toString()
            val downloadurl = myRef.downloadUrl.toString()
            val username = username_login.text.toString()
            val mydataRef = FirebaseDatabase.getInstance().getReference()
            mydataRef.child("users").child(uuid).child("username").setValue(username)**/
myRef.downloadUrl.addOnSuccessListener {
    Log.d("Main","url is : $it")
    InfotoDatabase(it.toString())
}
        }.addOnFailureListener {
            Toast.makeText(this,it.localizedMessage.toString(), Toast.LENGTH_LONG)
        }
    }
private fun InfotoDatabase(profileImageURL: String){
    val uid = FirebaseAuth.getInstance().uid
    val mydataRef = FirebaseDatabase.getInstance().getReference("/users/$uid")
val user = Users(uid!!, username_login.text.toString(), profileImageURL)
    mydataRef.setValue(user).addOnSuccessListener {
        Log.d("Main","successfully saved to database")
    val intent = Intent(applicationContext, NewMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
    } } }

