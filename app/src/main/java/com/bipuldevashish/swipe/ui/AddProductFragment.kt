package com.bipuldevashish.swipe.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bipuldevashish.swipe.R
import com.bipuldevashish.swipe.adapter.ImageAdapter
import com.bipuldevashish.swipe.databinding.FragmentAddProductBinding
import com.bipuldevashish.swipe.util.Resource
import com.bipuldevashish.swipe.util.Utils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private val viewModel by viewModel<MainActivityViewModel>()
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private var productType: String? = null
    private var images: ArrayList<Uri> = ArrayList()

    private val PICK_IMAGE_MULTIPLE = 1
    private lateinit var imagePath: String
    private var imagesPathList: MutableList<String> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        _binding = FragmentAddProductBinding.bind(view)

        setUpObserver()
        setUpListener()
    }

    /**
     * Gets the response updates to refresh the ui.
     */
    private fun setUpObserver() {
        viewModel.addProductResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    response.data!!.message.let {
                        Utils.show(it, requireActivity())
                    }
                    val action = AddProductFragmentDirections.actionAddProductFragmentToListProductFragment()
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                    response.message?.let {
                        Utils.show(it, requireActivity())
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    /**
     * Initializes all the user interactive elements
     */
    @SuppressLint("ObsoleteSdkInt")
    @Suppress("DEPRECATION")
    private fun setUpListener() {

        binding.radioGroup.setOnCheckedChangeListener { _, checkID ->
            findRadioButton(checkID)
        }

        binding.btnAddProduct.setOnClickListener {
            validateProduct()
        }

        binding.cardBtnAdd.setOnClickListener {
            if (Build.VERSION.SDK_INT < 19) {
                val intent = Intent()
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Picture")
                    , PICK_IMAGE_MULTIPLE
                )
            } else {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_MULTIPLE)
            }
        }
    }

    /**
     * Validates all the fields before calling the api.
     */
    private fun validateProduct() {
        if (productType.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select product type", Toast.LENGTH_SHORT)
                .show()
        } else if (binding.etProductName.text.isEmpty()) {
            binding.etProductName.error = "Please Enter Product Name"
        } else if (binding.etSellingPrice.text.isEmpty()) {
            binding.etSellingPrice.error = "Please Enter Product Selling Price"
        } else if (binding.etTaxAmount.text.isEmpty()) {
            binding.etTaxAmount.error = "Please Enter Product Tax"
        } else {
            uploadToServer()
        }
    }

    /**
     * Implements radio button click functionality
     * @param checkedId id of the checked radio button
     */
    private fun findRadioButton(checkedId: Int) {
        when (checkedId) {
            R.id.radio_product -> {
                productType = binding.radioProduct.text.toString()
            }
            R.id.radio_service -> {
                productType = binding.radioService.text.toString()
            }
            else -> {

            }
        }
    }

    /**
     * method to call api to upload files to server
     */
    private fun uploadToServer() {

        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("product_name", binding.etProductName.text.toString())
        builder.addFormDataPart("product_type", productType.toString())
        builder.addFormDataPart("price", binding.etSellingPrice.text.toString())
        builder.addFormDataPart("tax", binding.etTaxAmount.text.toString())

        for (i in 0 until imagesPathList.size) {
            val file = File(imagesPathList[i])
            builder.addFormDataPart(
                "file[]",
                file.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )
        }

        val requestBody = builder.build()
        viewModel.addProduct(requestBody)
    }

    /**
     * Refresh the ui after selecting images from gallery
     */
    private fun displayImageData() {

        binding.cardBtnAdd.visibility = View.GONE
        binding.cardRecyclerView.visibility = View.VISIBLE

        val imageAdapter = ImageAdapter(requireContext(), images)
        imageAdapter.items = images

        binding.recyclerImages.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = imageAdapter
        }
    }

    /**
     * Returns complete path from uri
     * @param uri
     */
    private fun getPathFromURI(uri: Uri) {
        val path: String = uri.path!! // uri = any content Uri

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val projection = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATE_TAKEN
            ) // some example data you can query
            val cursor = requireActivity().contentResolver.query(
                databaseUri,
                projection, selection, selectionArgs, null
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(projection[0])
                    imagePath = cursor.getString(columnIndex)
                    // Log.e("path", imagePath);
                    imagesPathList.add(imagePath)
                }
            }
            cursor?.close()
        } catch (e: Exception) {
            Log.e("TAG", e.message, e)
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        // When an Image is picked
        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK
            && null != data
        ) {
            if (data.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    images.add(imageUri)
                    getPathFromURI(imageUri)
                }
            } else if (data.data != null) {
                val uri = data.data
                if (uri != null) {
                    images.add(uri)
                }
                val imagePath: String = data.data!!.path!!
            }
            displayImageData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}