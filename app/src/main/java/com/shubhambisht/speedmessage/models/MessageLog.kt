package com.shubhambisht.speedmessage.models

class MessageLog(val id:String, val text: String, val FromId:String?, val toId:String, val timestamp:Long){
        constructor():this("","","","",-1)
    }