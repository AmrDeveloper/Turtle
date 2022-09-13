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
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.amrdeveloper.lilo.LiloException
import com.amrdeveloper.lilo.LiloInterpreter
import com.amrdeveloper.lilo.instruction.Instruction
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

    enum class EvaluatorState {
        NONE,
        RUNNING,
        FINISHED
    }

    enum class RenderState {
        NONE,
        RUNNING,
        FINISHED
    }

    private var renderState = RenderState.NONE
    private var evaluatorState = EvaluatorState.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        setupTurtleCanvasView()
        setupTurtleInterpreter()
        setupObservers()
        return binding.root
    }

    private fun setupTurtleCanvasView() {
        binding.turtleCanvasView.setOnRenderStartedListener(onCanvasRenderStartedListener)
        binding.turtleCanvasView.setOnRenderFinishedListener(onCanvasRenderFinishedListener)
    }

    private fun setupTurtleInterpreter() {
        liloInterpreter.setOnEvaluatorStartedListener(onEvaluatorStartedListener)
        liloInterpreter.setOnEvaluatorFinishedListener(onEvaluatorFinishedListener)
        liloInterpreter.setOnInstructionEmitterListener(onInstructionEmitterListener)
        liloInterpreter.setOnBackgroundChangeListener(onBackgroundChangeListener)
        liloInterpreter.setOnExceptionListener(onExceptionListener)
    }

    private fun setupObservers() {
        mainViewModel.liloScript.observe(viewLifecycleOwner) {
            mainViewModel.previewLiveData.value = false
            liloInterpreter.executeLiloScript(it)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val menu = menu.findItem(R.id.action_eval_or_stop)
        when (renderState) {
            RenderState.NONE -> {
                menu.isVisible = false
            }
            RenderState.RUNNING -> {
                menu.isVisible = true
                menu.setTitle(R.string.stop)
                menu.setIcon(R.drawable.ic_stop)
            }
            RenderState.FINISHED -> {
                menu.isVisible = true
                menu.setTitle(R.string.re_evaluate)
                menu.setIcon(R.drawable.ic_replay)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_preview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_eval_or_stop -> {
                if (renderState == RenderState.RUNNING) {
                    stopRunningScript()
                }

                if (renderState == RenderState.FINISHED){
                    executeLastScript()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun executeLastScript() {
        Timber.tag(TAG).d("Execute last script")
        binding.turtleCanvasView.setStoppingRenderScript(false)
        binding.turtleCanvasView.resetRenderAttributes()
        val lastScript = mainViewModel.liloScript.value ?: return
        liloInterpreter.executeLiloScript(lastScript)
        binding.turtleCanvasView.invalidate()
    }

    private fun stopRunningScript() {
        binding.turtleCanvasView.setStoppingRenderScript(true)
    }

    private val onCanvasRenderStartedListener = {
        Timber.tag(TAG).d("Render Started Listener")
        if (evaluatorState != EvaluatorState.NONE) {
            renderState = RenderState.RUNNING
            requireActivity().invalidateOptionsMenu()
        }
    }

    private val onCanvasRenderFinishedListener = {
        Timber.tag(TAG).d("Render Finished Listener")
        if (evaluatorState != EvaluatorState.NONE) {
            renderState = RenderState.FINISHED
            requireActivity().invalidateOptionsMenu()
        }
    }

    private val onEvaluatorStartedListener = {
        Timber.tag(TAG).d("Evaluator Started Listener")
        evaluatorState = EvaluatorState.RUNNING
    }

    private val onEvaluatorFinishedListener = {
        Timber.tag(TAG).d("Evaluator Finished Listener")
        evaluatorState = EvaluatorState.FINISHED
    }

    private val onInstructionEmitterListener = { instruction : Instruction ->
        Timber.tag(TAG).d("Emit Instruction")
        binding.turtleCanvasView.addInstruction(instruction)
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