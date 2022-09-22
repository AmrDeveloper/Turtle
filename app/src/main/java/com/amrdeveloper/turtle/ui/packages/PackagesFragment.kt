/*
 * MIT License
 *
 * Copyright (c) 2022 AmrDeveloper (Amr Hesham)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.amrdeveloper.turtle.ui.packages

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.data.LiloPackageListAdapter
import com.amrdeveloper.turtle.databinding.FragmentPackagesBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackagesFragment : Fragment() {

    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!

    private val packagesViewModel : PackagesViewModel by viewModels()

    private val packagesAdapter : LiloPackageListAdapter by lazy { LiloPackageListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPackagesBinding.inflate(inflater, container, false)
        setupPackagesList()
        setupObservers()
        packagesViewModel.loadLiloPackages()
        return binding.root
    }

    private fun setupPackagesList() {
        binding.packagesList.layoutManager = LinearLayoutManager(requireContext())
        binding.packagesList.adapter = packagesAdapter

        /**
         * Setup delete ons swapping and undo option to reinsert the deleted lilo package
         */
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
        val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.red))
        val swipeHandler = ItemSwipeCallback(deleteIcon, background) { holder ->
            val position = holder.absoluteAdapterPosition
            val liloPackage = packagesAdapter.currentList[position]
            val list = packagesAdapter.currentList.toMutableList()
            list.removeAt(position)
            packagesAdapter.submitList(list)

            packagesViewModel.deleteLiloPackage(liloPackage)

            val snackBar = Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                R.string.package_deleted_success,
                Snackbar.LENGTH_LONG
            )

            snackBar.apply {
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setActionTextColor(Color.GREEN)
                setBackgroundTint(ContextCompat.getColor(context, R.color.black))
                setAction(R.string.undo) {
                    list.add(position, liloPackage)
                    packagesAdapter.submitList(list)
                    packagesAdapter.notifyItemInserted(position)
                    packagesViewModel.insertLiloPackage(liloPackage)
                }
                show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.packagesList)

        packagesAdapter.setOnLiloPackageItemViewClickListener { item , _ ->
            val bundle = bundleOf("lilo_package" to item)
            findNavController().navigate(R.id.action_packagesFragment_to_editorFragment, bundle)
        }

        packagesAdapter.setOnLiloPackageItemViewLongClickListener { item , _ ->
            val bundle = bundleOf("lilo_package" to item)
            findNavController().navigate(R.id.action_packagesFragment_to_packageFragment, bundle)
            true
        }
    }

    private fun setupObservers() {
        packagesViewModel.liloPackagesLiveData.observe(viewLifecycleOwner) {
            packagesAdapter.submitList(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.queryHint = "Search keyword"
        searchView.setIconifiedByDefault(true)
        searchView.setOnQueryTextListener(searchViewQueryListener)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private val searchViewQueryListener = object : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(keyword: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (query.isNullOrEmpty()) packagesViewModel.loadLiloPackages()
            else packagesViewModel.loadLiloPackagesByKeyword(query)
            return false
        }
    }

    override fun onDestroyView() {
        binding.packagesList.adapter = null
        super.onDestroyView()
        _binding = null
    }
}