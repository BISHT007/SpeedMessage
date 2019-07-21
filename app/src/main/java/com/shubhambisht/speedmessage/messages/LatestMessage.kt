package com.shubhambisht.speedmessage.messages


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shubhambisht.speedmessage.R
import com.shubhambisht.speedmessage.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class LatestMessage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        supportActionBar?.title = "New Message"
        fetchUsers()
    }
    companion object{
        val USER_KEY = "USER_KEY"
    }
private fun fetchUsers(){
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object :ValueEventListener{
        override fun onDataChange(p0: DataSnapshot) {
            val adapter = GroupAdapter<ViewHolder>()
            // p0 is the snapshot that contain your data
            p0.children.forEach{
                Log.d("New Message",it.toString())
            val user = it.getValue(Users::class.java)
             if(user !=null ){adapter.add(UserItem(user))}

            }
            adapter.setOnItemClickListener { item, view ->
                val userItem = item as UserItem
                val intent = Intent(view.context,ChatLogActivity::class.java)
               intent.putExtra(USER_KEY,userItem.user)
            startActivity(intent)
            }

        recyclerview_Latestmessage.adapter = adapter
        }

        override fun onCancelled(p0: DatabaseError) {
        }
    })
}
}

    class UserItem(val user: Users):Item<ViewHolder>(){
        override fun getLayout(): Int {
        /// render rows in our recyceler view
 return R.layout.user_row_new_message
        }
        override fun bind(viewHolder: ViewHolder, position: Int) {
            // will be called in the list for each user object
        viewHolder.itemView.username_row_newmessage.text = user.username
            Picasso.get().load(user.profileImageURL).into(viewHolder.itemView.imageView_newmessage)

        }
    }
