package com.photosnap.savedresults

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.photosnap.R
import com.photosnap.databinding.FragmentSavedResultsBinding
import com.photosnap.util.setupToast

class SavedResultsFragment : Fragment() {

    private var savedResultsAdapter: SavedResultsAdapter? = null

    private lateinit var viewDataBinding: FragmentSavedResultsBinding

    private val viewModel:SavedResultsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_saved_results, container, false)

        viewDataBinding = FragmentSavedResultsBinding.bind(view).apply {
            this.viewmodel = viewModel
        }

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {

            savedResultsAdapter = SavedResultsAdapter(ArrayList(), viewModel, requireContext())
            viewDataBinding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            viewDataBinding.recyclerView.adapter = savedResultsAdapter
            (savedResultsAdapter as SavedResultsAdapter).notifyDataSetChanged()

            setUpObservers()

            setupSnackbar()

        }catch (e:Exception){

        }

    }

    private fun setUpObservers() {

        viewModel.updateList.observe(viewLifecycleOwner, Observer {
            if(viewModel.reverseImageLists.value!!.size>0){
                savedResultsAdapter!!.swapList(viewModel.reverseImageLists.value!!)
            }else{
                savedResultsAdapter!!.swapList(ArrayList())
            }
        })
    }

    private fun setupSnackbar() {
        view?.setupToast(this, viewModel.toastText, Toast.LENGTH_SHORT)
    }
}