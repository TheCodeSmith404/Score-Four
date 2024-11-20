package com.tcs.games.score4.ui.uploadimages

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentUploadImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadImage : Fragment(),OnUploadImageAdapterClickListener {
    private var _binding:FragmentUploadImageBinding?=null
    private val binding get()=_binding!!
    private val sharedViewModel:SharedImageViewModel by activityViewModels()
    private val viewModel:UploadImageViewModel by viewModels()
    private val selectImageSharedViewModel:SelectImageSharedViewModel by activityViewModels()
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { // Load image into ImageView
                selectImageSharedViewModel.imageUri.value=it // Store URI in ViewModel
                navigate()
            }
        }
    private lateinit var adapter:UploadImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding=FragmentUploadImageBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        // Inflate the layout for this fragment
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    override fun onDeleteClick(position: Int, imageId: Int) {
        TODO("Not yet implemented")
    }

    override fun onFooterClick() {
        selectImageLauncher.launch("image/*")
    }
    private fun navigate(){
        val bundle = Bundle().apply {
            putInt("sourceId", R.id.images_uploaded) // Pass the ID of the calling fragment
        }
        findNavController().navigate(R.id.action_images_upload_to_select_image,bundle)
    }
    private fun setUpRecyclerView(){
        adapter=UploadImageAdapter(requireContext(), mutableListOf("a","b","c","d",),6,this)
        binding.imagesRecycleView.layoutManager=GridLayoutManager(requireContext(),2)
        binding.imagesRecycleView.adapter=adapter
    }

}