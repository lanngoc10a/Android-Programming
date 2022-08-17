package com.photosnap.util

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


@SuppressLint("ResourceType")
fun View.showToast(snackbarText: String, timeLength: Int) {
    Toast.makeText(this.context,snackbarText,timeLength).show()

}


fun View.setupToast(
    lifecycleOwner: LifecycleOwner,
    snackbarEvent: LiveData<Event<String>>,
    timeLength: Int
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showToast(it, timeLength)
        }
    })
}
