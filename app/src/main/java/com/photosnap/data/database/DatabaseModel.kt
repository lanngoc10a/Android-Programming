package com.photosnap.data.database

data class DatabaseModel (
    var _id:Int=0,
    var downloadedImage:ByteArray,
    var downloadedImageUrl:String,
    var downloadedImageName:String
    )