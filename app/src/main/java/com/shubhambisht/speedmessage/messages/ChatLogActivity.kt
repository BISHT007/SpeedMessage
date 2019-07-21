package com.shubhambisht.speedmessage.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.shubhambisht.speedmessage.R
import com.shubhambisht.speedmessage.messages.LatestMessage.Companion.USER_KEY
import com.shubhambisht.speedmessage.models.ChatMessages
import com.shubhambisht.speedmessage.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {
companion object{
    val TAG = "chat log" }
    val adapter = GroupAdapter<ViewHolder>()
    var touser:Users? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        touser = intent.getParcelableExtra<Users>(LatestMessage.USER_KEY)
        supportActionBar?.title = touser?.username

        recyclerview_chatlog.adapter = adapter
        //setupdummydata()
        sendbutton_chatlog.setOnClickListener {
        PerformSendMessage()
}
        ListenforMessages()
    }



    private fun ListenforMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = touser?.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        reference.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessages = p0.getValue(ChatMessages::class.java)
                if (chatmessages != null) {
                    Log.d(TAG, chatmessages.text)
                    if (chatmessages.fromId == FirebaseAuth.getInstance().uid) {
                        val currentuser = NewMessageActivity.currentuser
                        adapter.add(ChatfromItem(chatmessages.text,currentuser!!))
                    } else {
                        adapter.add(ChattoItem(chatmessages.text,touser!!))
                    }
                }
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun PerformSendMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val user=  intent.getParcelableExtra<Users>(LatestMessage.USER_KEY)
        val toId = user.uid
        val text = entermessage_chatlog.text.toString()
        if(fromId == null) return
     //   val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toreference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatmessage = ChatMessages(reference.key!!,text,toId,fromId,System.currentTimeMillis()/1000)
        reference.setValue(chatmessage).addOnSuccessListener {
            Log.d(TAG,"saved mesages : ${reference.key}")
        entermessage_chatlog.text.clear()
        recyclerview_chatlog.scrollToPosition(adapter.itemCount -1)
        }
    toreference.setValue(chatmessage)

        val latestMessageref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageref.setValue(chatmessage)
        val latestMessagetoref = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessagetoref.setValue(chatmessage)
    }
//private fun setupdummydata(){
//        val adapter = GroupAdapter<ViewHolder>()
//        recyclerview_chatlog.adapter = adapter
//        adapter.add(ChatfromItem("hiiii,how are you?"))
//    adapter.add(ChattoItem("yo, i am good. you say?.its been long"))
//    adapter.add(ChatfromItem("yah yah,just got here.I was in US"))
//    adapter.add(ChattoItem("US!!!,great man.Its good to think that someone from our group is actually living abroad"))
//    }
}


class ChatfromItem(val text:String,val user:Users):Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.textView_from.text = text
        val uri = user.profileImageURL
        val targetimage = viewHolder.itemView.star_fom
        Picasso.get().load(uri).into(targetimage)

    }

}
class ChattoItem(val text:String,val user:Users):Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to.text = text
        val uri = user.profileImageURL
        val targetimage = viewHolder.itemView.star_to
        Picasso.get().load(uri).into(targetimage)
    }
}
