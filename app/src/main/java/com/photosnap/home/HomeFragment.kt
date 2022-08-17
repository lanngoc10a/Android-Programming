package com.photosnap.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.lyrebirdstudio.croppylib.Croppy
import com.lyrebirdstudio.croppylib.main.CropRequest
import com.photosnap.MainActivity
import com.photosnap.R
import com.photosnap.databinding.FragmentHomeBinding
import com.photosnap.util.EventObserver
import com.photosnap.util.Utility
import com.photosnap.util.setupToast
import kotlinx.android.synthetic.main.custom_choice_dialogue.view.*
import java.io.File


class HomeFragment : Fragment() {

    val AUTHORITY:String = "com.photosnap.home.HomeFragment"

    private lateinit var viewDataBinding: FragmentHomeBinding

    private val viewModel:HomeViewModel by viewModels()

    companion object{
        var Uploaded_Image_Url = ""
    }

    var photoFile: File? = null

    private val pickImageFromGallery_Code = 100
    private val pickImageFromCamera_Code = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewDataBinding = FragmentHomeBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToast()

        setUpListeners()

        MainActivity.mycallBack = object : MainActivity.callBackCropyImage {
            override fun takeUri(muri: Uri) {
                Log.v("Hello","Frag" + muri)
                val uri = Utility.readUriImage(requireContext(), muri!!)
                viewDataBinding.selectedImage.setImageURI(uri)
                viewModel.setProductImageUri(uri!!)
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setUpListeners() {
        viewModel.openChoiceDialogue.observe(viewLifecycleOwner, EventObserver {
            showImageChoiceDialogue()
        })
        viewDataBinding.cropImage.setOnClickListener {

                if (!viewModel.getProductImageUri().toString().isNullOrEmpty()) {
                    val cropRequest = CropRequest.Auto(
                        sourceUri = viewModel.getProductImageUri()!!,
                        requestCode = 102
                    )
                    Croppy.start(requireActivity(), cropRequest)
                } else {
                    viewModel.showSnackbarMessage("Image Not Selected")
                }

        }
        viewDataBinding.clearImage.setOnClickListener {
            viewDataBinding.selectedImage.setImageResource(0)
            viewModel.setProductImageUri("".toUri())
            Uploaded_Image_Url=""
            viewModel.showSnackbarMessage("Image Cleared")
        }

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                viewDataBinding.progressBar.visibility = View.VISIBLE
            }else{
                viewDataBinding.progressBar.visibility = View.GONE
            }
        })

        viewModel.productImageUri.observe(viewLifecycleOwner, Observer {
            if(!it.toString().isNullOrEmpty()){
                viewDataBinding.selectedImage.setImageURI(it)
            }
        })
    }

    private fun setupToast() {
        view?.setupToast(this, viewModel.toastText, Toast.LENGTH_SHORT)
    }

    override fun onResume() {
        super.onResume()
        if(!viewModel.toastText.hasActiveObservers())
            setupToast()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showImageChoiceDialogue(){
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.getLayoutInflater()
        val dialogView = inflater.inflate(R.layout.custom_choice_dialogue, null)
        dialogBuilder.setView(dialogView)

        val btn_camera = dialogView.btn_camera
        val btn_gallery = dialogView.btn_gallery
        val txt_dialog_title = dialogView.txt_dialog_content
        val alertDialog = dialogBuilder.create()
        txt_dialog_title.setText("Select Image From")

        btn_camera.setOnClickListener {
            alertDialog.dismiss()
            onLaunchCamera()
        }

        btn_gallery.setOnClickListener {
            alertDialog.dismiss()
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageFromGallery_Code)
        }

        alertDialog.show()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageFromGallery_Code) {
            var imageUri: Uri? = data?.data
            val uri = Utility.readUriImage(requireContext(),imageUri!!)
            viewDataBinding.selectedImage.setImageURI(uri)
            viewModel.setProductImageUri(uri!!)
        }

        if (requestCode == pickImageFromCamera_Code && resultCode == Activity.RESULT_OK) {

            val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
            viewModel.setProductImageUri(photoFile!!.toUri())

            viewDataBinding.selectedImage.setImageBitmap(Utility.rotateImageIfRequired(takenImage,photoFile!!.toUri()))
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun onLaunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile =  Utility.createFileForImage(requireContext())
        if (photoFile != null) {
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), AUTHORITY, photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {

                startActivityForResult(intent, pickImageFromCamera_Code)
            }
        }
    }
}