package com.photosnap.savedresults

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.photosnap.data.database.DatabaseModel
import com.photosnap.data.database.ReverseDbHelper
import com.photosnap.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class SavedResultsViewModel:ViewModel() {


    private val _reverseImageLists = MutableLiveData<ArrayList<DatabaseModel>>()
    val reverseImageLists: LiveData<ArrayList<DatabaseModel>> = _reverseImageLists

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> = _showLoading

    private val _updateList = MutableLiveData<Event<String>>()
    val updateList: LiveData<Event<String>> = _updateList


    fun showSnackbarMessage(message: String) { //
        _toastText.value = Event(message)
    }

    fun getAllImagesListFromDb(){
        try {
            GlobalScope.launch(Dispatchers.IO) {
                val list = ReverseDbHelper.getAllImages()
                launch (Dispatchers.Main){
                    _reverseImageLists.value = ArrayList()
                    _reverseImageLists.value!!.addAll(list)
                    _updateList.value = Event("Update")
                }
            }
        }catch (e:Exception){

        }
    }

    fun deleteItem(id:Int){
        try {
            GlobalScope.launch(Dispatchers.IO) {
                ReverseDbHelper.deleteReverseImageData(id)

                launch(Dispatchers.Main) {
                    getAllImagesListFromDb()
                    showSnackbarMessage("Image Deleted")
                }
            }
        }catch (e:Exception){

        }
    }

}