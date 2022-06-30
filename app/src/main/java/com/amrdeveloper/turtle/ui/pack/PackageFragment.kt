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

package com.amrdeveloper.turtle.ui.pack

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.data.LiloPackage
import com.amrdeveloper.turtle.databinding.FragmentPackageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PackageFragment : Fragment() {

    private var _binding: FragmentPackageBinding? = null
    private val binding get() = _binding!!

    private val packageViewModel: PackageViewModel by viewModels()

    private var liloSourceCode : String = ""
    private lateinit var currentLiloPackage : LiloPackage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        (arguments?.get("source_code") as String?)?.let {
            liloSourceCode = it
        }

        (arguments?.get("lilo_package") as LiloPackage?)?.let {
            currentLiloPackage = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPackageBinding.inflate(inflater, container, false)

        if (::currentLiloPackage.isInitialized) {
            binding.packageNameText.setText(currentLiloPackage.name)
        }

        return binding.root
    }

   override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_package, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                if (::currentLiloPackage.isInitialized) updateCurrentPackage()
                else saveNewPackage()
                findNavController().navigateUp()
                true
            }
            R.id.action_close -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNewPackage() {
        val name = binding.packageNameText.text.toString().trim()
        if (name.isEmpty()) {
            binding.packageNameText.error = "Name can't be empty"
            return
        }
        val liloPackage = LiloPackage(name, liloSourceCode)
        packageViewModel.savePackage(liloPackage)
    }

    private fun updateCurrentPackage() {
        val name = binding.packageNameText.text.toString().trim()
        if (name.isEmpty()) {
            binding.packageNameText.error = "Name can't be empty"
            return
        }
        currentLiloPackage.name = name
        packageViewModel.updatePackage(currentLiloPackage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}