package com.bipuldevashish.swipe.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bipuldevashish.swipe.R
import com.bipuldevashish.swipe.adapter.ProductListAdaptor
import com.bipuldevashish.swipe.databinding.FragmentListProductBinding
import com.bipuldevashish.swipe.util.Resource
import com.bipuldevashish.swipe.util.Utils.show
import org.koin.androidx.viewmodel.ext.android.viewModel


class ListProductFragment : Fragment(R.layout.fragment_list_product) {

    private val viewModel by viewModel<MainActivityViewModel>()
    private lateinit var adapterProduct: ProductListAdaptor
    private var _binding: FragmentListProductBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        _binding = FragmentListProductBinding.bind(view)

        adapterProduct = ProductListAdaptor(listOf(), requireContext())

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = adapterProduct
        }

        setUpObserver()
        setUpListeners()
        fetchList()

    }

    private fun fetchList() {
        viewModel.getProductList()
    }

    /**
     * For navigation from one List Fragment to Add new product fragment
     */

    private fun setUpListeners() {

        binding.extendedFab.setOnClickListener{
            val action = ListProductFragmentDirections.actionListProductFragmentToAddProductFragment()
            findNavController(requireActivity(),R.id.nav_host_fragment).navigate(action)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObserver() {

        /**
         * We observe the data to populate the recycler view and Make the visibility of the progress bar gone.
         */
        viewModel.productList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapterProduct.items = response.data!!
                    adapterProduct.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE

                    response.message?.let {
                        show(it, requireContext())
                    }
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}