package com.photosnap.home

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photosnap.data.remote.ReverseImageRetreiver
import com.photosnap.home.HomeFragment.Companion.Uploaded_Image_Url
import com.photosnap.util.Event
import kotlinx.coroutines.launch
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.SocketTimeoutException


class HomeViewModel:ViewModel() {

    private val reverseImageRetreiver: ReverseImageRetreiver = ReverseImageRetreiver()


    // Two-way databinding, exposing MutableLiveData
    val productImageUri = MutableLiveData<Uri>()

    private val _openChoiceDialogue = MutableLiveData<Event<Unit>>()
    val openChoiceDialogue: LiveData<Event<Unit>> = _openChoiceDialogue
    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    init {
        productImageUri.value = "".toUri()
    }

    fun onclickAddImage(){
        _openChoiceDialogue.value = Event(Unit)
    }

    fun setProductImageUri(uri: Uri){
        productImageUri.value = uri
    }

    fun getProductImageUri(): Uri? {
        return productImageUri.value
    }

    fun showSnackbarMessage(message: String) { //
        _toastText.value = Event(message)
    }

    fun uploadImage(){
        if(productImageUri.value==null){
            showSnackbarMessage("Image Not Selected")
        }else{
            viewModelScope.launch {
                _showLoading.value  = true
                val cp = productImageUri.value!!.path
                val file = File(cp)

                val reqFile: RequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
                val body = MultipartBody.Part.createFormData("image", file.getName(), reqFile)

                val call: Call<ResponseBody> = reverseImageRetreiver.uploadInverseImage(body)

                call.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(
                        call: Call<ResponseBody?>,
                        response: Response<ResponseBody?>
                    ) {
                        _showLoading.value  = false
                        if (response.isSuccessful()) {

                            val html = response.body()!!.string()
                            val document: Document = Jsoup.parse(html)
                            Uploaded_Image_Url = document.body().text()

                            showSnackbarMessage("Image Uploaded Successfully !")


                        } else {
                            Log.d("UploadImage", "Response failure = " + response.message())
                            showSnackbarMessage("${response.message()} !")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        _showLoading.value  = false
                        if (t is SocketTimeoutException) {
                            // "Connection Timeout";
                            showSnackbarMessage("Connection Timeout !")
                            Log.e("UploadImage", "Connection Timeout")
                        } else if (t is IOException) {
                            // "Timeout";
                            showSnackbarMessage("Timeout !")
                            Log.e("UploadImage", "Timeout")
                        } else {
                            //Call was cancelled by user
                            if (call.isCanceled) {
                                showSnackbarMessage("Call was cancelled forcefully !")
                                Log.e("UploadImage", "Call was cancelled forcefully")
                            } else {
                                showSnackbarMessage("Network Error :: ${t.localizedMessage}!")
                                //Generic error handling
                                Log.e("UploadImage", "Network Error :: " + t.localizedMessage)
                            }
                        }
                    }
                })
            }
        }
    }
}