package com.shubhambisht.speedmessage.messages

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.firebase.auth.FirebaseAuth
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseError
import com.google.firebase.database.*
import com.shubhambisht.speedmessage.R
import com.shubhambisht.speedmessage.authentication.MainActivity
import com.shubhambisht.speedmessage.models.ChatMessages
import com.shubhambisht.speedmessage.models.LatestMessagesRow
import com.shubhambisht.speedmessage.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.latest_messages_row.view.*
import com.shubhambisht.speedmessage.messages.LatestMessage.Companion.USER_KEY as LatestMessageUSER_KEY

class NewMessageActivity : AppCompatActivity() {
    companion object {
        var currentuser: Users? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        recyclerview_new_messages.adapter = adapter
        recyclerview_new_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        //set on item click listner on aadapter
        verifyUserisLoggedIn()
        //  setupdummyrows()
        //    Log.d("latest","exception is : ${e.printStackTrace()}")
        listenforlatestmessages()
        fetchcurrentuser()
        adapter.setOnItemClickListener { item, view ->
          val row = item as LatestMessagesRow
            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(LatestMessage.USER_KEY,row.chatpartneruser)
            startActivity(intent)
        }

    }

    private fun fetchcurrentuser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                currentuser = p0.getValue(Users::class.java)
                Log.d("newmessages", "current user is :${currentuser?.profileImageURL}")
            } }) }

    private fun verifyUserisLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
    val adapter = GroupAdapter<ViewHolder>()
    private fun setupdummyrows() {

        /**  adapter.add(LatestMessagesRow())
        adapter.add(LatestMessagesRow())
        adapter.add(LatestMessagesRow())
        adapter.add(LatestMessagesRow())**/
    }
    val messagesMap = HashMap<String, ChatMessages>()
    private fun RefreshRecyclerViewMessages() {
        adapter.clear()
        messagesMap.values.forEach {
            adapter.add(LatestMessagesRow(it))
        }
    }


    private fun listenforlatestmessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {


            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessages = p0.getValue(ChatMessages::class.java) ?: return
                //  val users = p0.getValue(Users::class.java) ?:return
                //   if(chatMessages == null) return
                messagesMap[p0.key!!] = chatMessages
                RefreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.new_message -> {
                val intent = Intent(this, LatestMessage::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}



