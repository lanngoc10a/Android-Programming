package com.photosnap.reverseimageresult

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosnap.data.database.DatabaseModel
import com.photosnap.data.database.ReverseDbHelper
import com.photosnap.data.model.ServerResponse
import com.photosnap.data.remote.ReverseImageRetreiver
import com.photosnap.home.HomeFragment.Companion.Uploaded_Image_Url
import com.photosnap.util.Event
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.URL

class ReverseImageResultViewModel:ViewModel() {

    private val reverseImageRetreiver: ReverseImageRetreiver = ReverseImageRetreiver()

    val selectedMode = MutableLiveData<String>()

    private val _reverseImageLists = MutableLiveData<ArrayList<ServerResponse>>()
    val reverseImageLists: LiveData<ArrayList<ServerResponse>> = _reverseImageLists

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _updateList = MutableLiveData<Event<String>>()
    val updateList: LiveData<Event<String>> = _updateList

    fun showSnackbarMessage(message: String) { //
        _toastText.value = Event(message)
    }

    init {
        selectedMode.value = ""
    }

    fun clearResults(){
        _reverseImageLists.value = ArrayList()
        _updateList.value = Event("Update")
    }
    fun onclickSearch(){
        try {
            if (!Uploaded_Image_Url.isNullOrEmpty()) {
                viewModelScope.launch {
                    _reverseImageLists.value = ArrayList()
                    _updateList.value = Event("Update")
                    _showLoading.value = true
                    if (selectedMode.value.toString().equals("Google")) {
                        var res =
                            reverseImageRetreiver.googleInverseImage(Uploaded_Image_Url)
                        _reverseImageLists.value!!.addAll(res)
                    } else if (selectedMode.value.toString().equals("Bing")) {
                        var res =
                            reverseImageRetreiver.bingInverseImage(Uploaded_Image_Url)
                        _reverseImageLists.value!!.addAll(res)
                    } else if (selectedMode.value.toString().equals("Tineye")) {
                        var res =
                            reverseImageRetreiver.tineyeInverseImage(Uploaded_Image_Url)
                        _reverseImageLists.value!!.addAll(res)
                    } else {
                        showSnackbarMessage("No Server Selected")
                    }
                    _updateList.value = Event("Update")
                    _showLoading.value = false
                }
            } else {
                showSnackbarMessage("No Image Uploaded")
            }
        }catch (e:Exception){

        }
    }

    fun takeUrl(currentItem:ServerResponse) {
        try {
            _showLoading.value = true
            GlobalScope.launch(Dispatchers.IO) {
                val res = ReverseDbHelper.insertReverseImage(
                    DatabaseModel(
                        0,
                        convertToByteArray(currentItem.image_link),
                        currentItem.image_link,
                        currentItem.name
                    )
                )
                launch(Dispatchers.Main) {
                    showSnackbarMessage("Image Downloaded Successfully")
                }
            }

            _showLoading.value = false
        }catch (e:Exception){

        }
    }

    suspend fun convertToByteArray(url:String): ByteArray {
        val baos = ByteArrayOutputStream()
        val bitmap = getImageBitmap(url)
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val photo: ByteArray = baos.toByteArray()
        return photo
    }

    suspend fun getImageBitmap(url:String): Bitmap? {
        try {
            var image: Bitmap? = null
            withContext(Dispatchers.IO) {
                val url = URL(url)
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            }
            return image
        } catch (e: IOException) {
            System.out.println(e)
        }
        return null
    }
}