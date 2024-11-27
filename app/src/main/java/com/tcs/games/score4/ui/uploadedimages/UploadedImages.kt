package com.tcs.games.score4.ui.uploadedimages

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentUploadImageBinding
import com.tcs.games.score4.ui.selectimage.SelectImageSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.ImageUtils
import utils.constants.ImageNames

@AndroidEntryPoint
class UploadedImages : Fragment(),OnUploadImageAdapterClickListener {
    private var _binding:FragmentUploadImageBinding?=null
    private val binding get()=_binding!!
    private val viewModel:UploadedImagesViewModel by viewModels()
    private val selectImageSharedViewModel: SelectImageSharedViewModel by activityViewModels()
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { // Load image into ImageView
                selectImageSharedViewModel.imageUri.value=it // Store URI in ViewModel
                navigate(viewModel.currentImageId)
            }
        }
    private lateinit var adapter:UploadedImagesAdapter
    private val uploadedImagesSharedViewModel:UploadedImagesSharedViewModel by activityViewModels()
    private val job:Job by lazy{ Job()}
    private val customImageScope:CoroutineScope by lazy { CoroutineScope(Dispatchers.IO + job) }


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
        setUpImageSelector()
        setUpOnClickListeners()
        downloadImages()
        setUpRecyclerView()
        observeImageChanges()
        interceptBackPresses()
    }
    override fun onItemClick(position: Int,imageId:Int){
        if(binding.actionDone.visibility==View.GONE){
            binding.actionDone.visibility=View.VISIBLE
        }
        viewModel.showWarning=true
        val cards=uploadedImagesSharedViewModel.cardId.value
        var card=cards[viewModel.currentCard]!!
        card=Pair(imageId,card.second)
        cards[viewModel.currentCard]=card
        uploadedImagesSharedViewModel.updateImageData(cards)
        val cardInitial=viewModel.getCardInitialFromId(viewModel.currentCard).toString()
        adapter.updateCardItem(position,imageId,cardInitial,cards[viewModel.currentCard]!!.second)
        binding.currentCard.text=cardInitial
        viewModel.currentCard=++viewModel.currentCard%4
    }

    override fun onDeleteClick(position: Int, imageId: Int) {

    }

    override fun onFooterClick() {
        viewModel.currentImageId=viewModel.getImageIdToUpload()
        selectImageLauncher.launch("image/*")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    private fun interceptBackPresses(){
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Intercept the back press
                    if(viewModel.showWarning){
                        showUpdateDialog()
                    }else{
                        isEnabled=false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }
    private fun setUpOnClickListeners(){
        binding.actionCancel.setOnClickListener{
            if(viewModel.showWarning){
                showUpdateDialog()
            }
        }
        binding.actionDone.setOnClickListener{
            imagesUpdated()
            findNavController().navigateUp()
        }
    }
    private fun showUpdateDialog(){
        Snackbar.make(requireView(),"Please save the changes",Snackbar.LENGTH_LONG)
            .setAction("Go Back"){
                findNavController().navigateUp()
            }.show()
    }
    private fun setUpImageSelector(){
        viewModel.currentCard=uploadedImagesSharedViewModel.cardId.value[-1]!!.first
        binding.currentCard.text=viewModel.getCardInitialFromId(viewModel.currentCard).toString()
    }
    private fun navigate(imageId:Int){
        val bundle = Bundle().apply {
            putString("imageName","${ImageNames.CARD.txt}$imageId")
            putInt("sourceId", R.id.images_uploaded) // Pass the ID of the calling fragment
        }
        findNavController().navigate(R.id.action_images_upload_to_select_image,bundle)
    }
    private fun setUpResultListener(){
        setFragmentResultListener("selectImage") { requestKey, bundle ->
            val done = bundle.getBoolean("done", false)
            if (done) {
                viewModel.updateImagesList(viewModel.currentImageId)
                // Handle successful result
                Toast.makeText(requireContext(), "Image selection completed!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Image selection not completed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun imagesUpdated(){
        if(!uploadedImagesSharedViewModel.imagesChanged.value){
            uploadedImagesSharedViewModel.imagesChanged.value=true
        }
    }
    private fun setUpRecyclerView(){
        adapter=UploadedImagesAdapter(requireActivity().application,requireContext(), viewModel.getImageList(),
            transformMap(),viewModel.getMaxAllowedImages(),customImageScope,this)
        binding.imagesRecycleView.layoutManager=GridLayoutManager(requireContext(),2)
        binding.imagesRecycleView.adapter=adapter
    }
    private fun transformMap(): MutableMap<Int, Pair<String, Int>> {
        // Assuming cardId.value is a map of type Map<Int, Pair<Int, Int>>
        val cardMap = uploadedImagesSharedViewModel.cardId.value ?: emptyMap()

        // Transform the map using getCardInitialFromId for the first Pair value
        return cardMap
            .filter { (_, value) -> value.first > 0 } // Filter entries where `first` > 0
            .map { (key, value) ->
                val (first, second) = value
                val transformedFirst = viewModel.getCardInitialFromId(key).toString()
                first to (transformedFirst to second) // Transform the value
            }
            .toMap()
            .toMutableMap()
    }
    private fun downloadImages(){
        if(!viewModel.isImagesUploaded()&&viewModel.getImageList().size!=0){
            viewModel.downloadMultipleImages(requireContext(),
                {progress->
                    Log.d("downloadingImages",progress.toString())
                },
                {done->
                    viewModel.imageUploaded()
                    Log.d("Null pointer","Updating adapter")
                    adapter.updateImageData(viewModel.getImageList(),viewModel.currentImageId)
                    Log.d("downloadingImages",done.toString())
                })
        }else{
            viewModel.imageUploaded()
        }
    }
    private fun observeImageChanges(){
       lifecycleScope.launch{
           lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
               selectImageSharedViewModel.isImageUploaded.collect{value->
                   when(value){
                       true->{
                           viewModel.updateImagesList(viewModel.currentImageId)
                           selectImageSharedViewModel.updateImageUploaded(false)
                       }
                       false->{
                           // Do nothing
                       }
                   }
               }

           }
       }
    }


}