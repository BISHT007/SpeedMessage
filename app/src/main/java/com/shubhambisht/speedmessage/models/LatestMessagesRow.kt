package com.shubhambisht.speedmessage.models

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shubhambisht.speedmessage.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_messages_row.view.*

class LatestMessagesRow(val chatMessages: ChatMessages) : Item<ViewHolder>() {
       var chatpartneruser:Users? = null
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.textview_latest_messagses.text = chatMessages.text
            val chatpartnerid: String
            if (chatMessages.fromId == FirebaseAuth.getInstance().uid) {
                chatpartnerid = chatMessages.toId
            } else chatpartnerid = chatMessages.fromId

            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatpartnerid")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatpartneruser = p0.getValue(Users::class.java)
                    viewHolder.itemView.username_latestmessages.text = chatpartneruser?.username
                    val targetimage = viewHolder.itemView.imageView_latestmessages
                    Picasso.get().load(chatpartneruser?.profileImageURL).into(targetimage)
                }
            })
        }

        override fun getLayout(): Int {
            return R.layout.latest_messages_row
        }
    }