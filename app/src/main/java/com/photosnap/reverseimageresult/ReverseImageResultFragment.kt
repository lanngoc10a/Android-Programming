package com.photosnap.reverseimageresult

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photosnap.R
import com.photosnap.databinding.FragmentReverseImageResultBinding
import com.photosnap.util.setupToast


class ReverseImageResultFragment : Fragment(), AdapterView.OnItemSelectedListener  {

    private var reverseOnlineResourceList: MutableList<String> = mutableListOf<String>()

    private lateinit var viewDataBinding: FragmentReverseImageResultBinding

    private val viewModel:ReverseImageResultViewModel by viewModels()

    private var reverseImagesRecyclerViewAdapter: ReverseImagesRecyclerViewAdapter? = null

    private var selectedResource = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reverse_image_result, container, false)

        viewDataBinding = FragmentReverseImageResultBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            reverseOnlineResourceList.add("None")
            reverseOnlineResourceList.add("Google")
            reverseOnlineResourceList.add("Bing")
            reverseOnlineResourceList.add("Tineye")

            reverseImagesRecyclerViewAdapter =
                ReverseImagesRecyclerViewAdapter(ArrayList(), viewModel, requireContext())
            viewDataBinding.productsList.layoutManager = GridLayoutManager(requireContext(), 2)
            viewDataBinding.productsList.adapter = reverseImagesRecyclerViewAdapter
            (reverseImagesRecyclerViewAdapter as ReverseImagesRecyclerViewAdapter).notifyDataSetChanged()

            setUpObservers()

            setupSnackbar()

            viewDataBinding.textView34.onItemSelectedListener = this

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.server_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                viewDataBinding.textView34.adapter = adapter
            }
        }catch (e:Exception){

        }
    }

    private fun setUpObservers() {
        viewModel.updateList.observe(viewLifecycleOwner, Observer {
            if(viewModel.reverseImageLists.value!!.size>0){

                viewDataBinding.progressBar.visibility = View.GONE
                reverseImagesRecyclerViewAdapter!!.swapList(viewModel.reverseImageLists.value!!)
            }else{
                reverseImagesRecyclerViewAdapter!!.swapList(ArrayList())
            }
        })

        viewModel.showLoading.observe(viewLifecycleOwner, Observer {
            if(it){
                viewDataBinding.progressBar.visibility = View.VISIBLE
            }else{
                viewDataBinding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun setupSnackbar() {
        view?.setupToast(this, viewModel.toastText, Toast.LENGTH_SHORT)
    }


    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        if(parent.getChildAt(0)!=null)
            (parent.getChildAt(0) as TextView).setTextColor(Color.parseColor("#ffffff"))
        selectedResource = reverseOnlineResourceList[pos]
        if(selectedResource!="None"){
            viewModel.selectedMode.value = selectedResource
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

}
