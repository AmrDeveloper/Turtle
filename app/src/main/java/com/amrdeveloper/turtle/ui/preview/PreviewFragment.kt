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

package com.amrdeveloper.turtle.ui.preview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.amrdeveloper.lilo.LiloException
import com.amrdeveloper.lilo.LiloInterpreter
import com.amrdeveloper.lottiedialog.LottieDialog
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.databinding.FragmentPreviewBinding
import com.amrdeveloper.turtle.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val TAG = "PreviewFragment"

@AndroidEntryPoint
class PreviewFragment : Fragment() {

    private var _binding: FragmentPreviewBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel : MainViewModel by activityViewModels()
    private val liloInterpreter: LiloInterpreter by lazy { LiloInterpreter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        setupTurtleInterpreter()
        setupTurtleCanvasView()
        setupObservers()
        return binding.root
    }

    private fun setupTurtleCanvasView() {
        binding.turtleCanvasView.setLiloInterpreter(liloInterpreter)
    }

    private fun setupTurtleInterpreter() {
        liloInterpreter.setOnDegreeChangeListener(onDegreeChangeListener)
        liloInterpreter.setOnBackgroundChangeListener(onBackgroundChangeListener)
        liloInterpreter.setOnExceptionListener(onExceptionListener)
    }

    private fun setupObservers() {
        mainViewModel.liloScript.observe(viewLifecycleOwner) {
            mainViewModel.previewLiveData.value = false
            binding.turtleCanvasView.loadLiloScript(it)
        }
    }

    private val onDegreeChangeListener = { degree : Float ->
        binding.turtlePointer.rotation = degree - 180
    }

    private val onBackgroundChangeListener = { color : Int ->
        binding.turtleCanvasView.setBackgroundColor(color)
    }

    private val onExceptionListener = { exception : LiloException ->
        Timber.tag(TAG).d("Lilo Runtime exception ${exception.message}")
        launchLiloExceptionDialog(exception)
    }

    private fun launchLiloExceptionDialog(exception: LiloException) {
        val lottieDialog = LottieDialog(requireContext())
            .setAnimation(R.raw.turtle_flail)
            .setAnimationRepeatCount(LottieDialog.INFINITE)
            .setAutoPlayAnimation(true)
            .setTitle("Runtime Exception")
            .setTitleColor(ContextCompat.getColor(requireContext(), R.color.monokia_pro_pink))
            .setDialogBackground(ContextCompat.getColor(requireContext(), R.color.monokia_pro_black))
            .setMessage("${exception.message} at line ${exception.position.line}")
            .setMessageColor(ContextCompat.getColor(requireContext(), R.color.monokia_pro_purple))
        lottieDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}