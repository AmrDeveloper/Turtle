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

package com.amrdeveloper.turtle.ui.editor

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amrdeveloper.treeview.TreeViewAdapter
import com.amrdeveloper.treeview.TreeViewHolderFactory
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.databinding.FragmentEditorBinding
import com.amrdeveloper.turtle.externsions.toTreeNodes
import com.amrdeveloper.turtle.ui.MainViewModel
import timber.log.Timber

private const val TAG = "EditorFragment"

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel : MainViewModel by activityViewModels()

    private lateinit var treeViewAdapter: TreeViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        configCodeView()
        setupDiagnosticsTreeView()
        setupObservers()
        return binding.root
    }

    private fun configCodeView() {
        binding.editorView.setEnableLineNumber(true)
        binding.editorView.setLineNumberTextColor(Color.GRAY)
        binding.editorView.setLineNumberTextSize(25f)
        configCodeViewForTurtle(binding.editorView)
        binding.editorView.reHighlightSyntax()
    }

    private fun setupDiagnosticsTreeView() {
        val treeViewFactory = TreeViewHolderFactory { view, _ -> DiagnosticViewHolder(view) }
        treeViewAdapter = TreeViewAdapter(treeViewFactory)

        binding.diagnosticsList.layoutManager = LinearLayoutManager(requireContext())
        binding.diagnosticsList.isNestedScrollingEnabled = false
        binding.diagnosticsList.adapter = treeViewAdapter
    }

    private fun setupObservers() {
        mainViewModel.previewLiveData.observe(viewLifecycleOwner) { shouldPreview ->
            if (shouldPreview) {
                findNavController().navigate(R.id.action_editorFragment_to_previewFragment)
            }
        }

        mainViewModel.diagnosticsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                treeViewAdapter.updateTreeNodes(listOf())
                binding.titleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            } else {
                treeViewAdapter.updateTreeNodes(it.toTreeNodes(R.layout.list_item_diagnostic))
                binding.titleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editor, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_execute -> {
                executeCurrentScript()
                true
            }
            R.id.action_save -> {
                saveCurrentScript()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun executeCurrentScript() {
        val script = binding.editorView.textWithoutTrailingSpace.trim()
        if (script.isNotEmpty()) {
            Timber.tag(TAG).d("Execute the current script")
            mainViewModel.executeLiloScript(script)
        }
    }

    private fun saveCurrentScript() {
        val script = binding.editorView.textWithoutTrailingSpace.trim()
        if (script.isNotEmpty()) {
            // TODO: send it to PackageFragment to set title and save it
            Timber.tag(TAG).d("Save the current script")
        }
    }

    override fun onDestroyView() {
        binding.diagnosticsList.adapter = null
        super.onDestroyView()
        _binding = null
    }
}