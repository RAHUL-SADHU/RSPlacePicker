package com.app.rspicker

interface LocationConfirmListener {

    fun locationConfirm(address:String,latitude:Double,longitude:Double,imageUrl:String)
}