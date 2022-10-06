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
import android.widget.SeekBar
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
        STOPPED,
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
        setupTurtleExecutionSeekbar()
        setupObservers()

        return binding.root
    }

    private fun setupTurtleCanvasView() {
        binding.turtleCanvasView.setOnRenderStartedListener(onCanvasRenderStartedListener)
        binding.turtleCanvasView.setOnRenderStoppedListener(onCanvasRenderStoppedListener)
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

    private fun setupTurtleExecutionSeekbar() {
        binding.executionBar.visibility = View.GONE

        binding.executionBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Timber.tag(TAG).d("onProgressChanged")
                binding.turtleCanvasView.setInstructionLimit(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val evanStopMenuAction = menu.findItem(R.id.action_eval_or_stop)
        prepareEvalStopMenuAction(evanStopMenuAction)

        val pauseResumeMenuAction = menu.findItem(R.id.action_resume_or_pause)
        preparePauseResumeMenuAction(pauseResumeMenuAction)
    }

    private fun prepareEvalStopMenuAction(item: MenuItem) {
        when (renderState) {
            RenderState.NONE -> {
                item.isVisible = false
            }
            RenderState.RUNNING -> {
                item.isVisible = true
                item.setTitle(R.string.stop)
                item.setIcon(R.drawable.ic_stop)
            }
            RenderState.STOPPED -> {
                item.isVisible = true
                item.setTitle(R.string.stop)
                item.setIcon(R.drawable.ic_replay)
            }
            RenderState.FINISHED -> {
                item.isVisible = true
                item.setTitle(R.string.re_evaluate)
                item.setIcon(R.drawable.ic_replay)
            }
        }
    }

    private fun preparePauseResumeMenuAction(item : MenuItem) {
        when (renderState) {
            RenderState.NONE -> {
                item.isVisible = false
            }
            RenderState.RUNNING -> {
                item.isVisible = false
            }
            RenderState.STOPPED -> {
                item.isVisible = true
                item.setIcon(R.drawable.ic_resume)
            }
            RenderState.FINISHED -> {
                item.isEnabled = false
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
                if (renderState == RenderState.RUNNING || renderState == RenderState.STOPPED) {
                    stopCurrentScript()
                }
                if (renderState == RenderState.STOPPED) {
                    binding.turtleCanvasView.setStoppingRenderScript(true)
                    executeLastScript()
                }
                if (renderState == RenderState.FINISHED) {
                    executeLastScript()
                }
                return true
            }
            R.id.action_resume_or_pause -> {
                if (renderState == RenderState.RUNNING) {
                    pauseCurrentScript()
                }
                if (renderState == RenderState.STOPPED) {
                    resumeCurrentScript()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun executeLastScript() {
        Timber.tag(TAG).d("Execute Start")
        binding.turtleCanvasView.setStoppingRenderScript(false)
        binding.turtleCanvasView.resetRenderAttributes()
        val lastScript = mainViewModel.liloScript.value ?: return
        liloInterpreter.executeLiloScript(lastScript)
        binding.turtleCanvasView.invalidate()
    }

    private fun pauseCurrentScript() {
        Timber.tag(TAG).d("Execute Pause")
        binding.turtleCanvasView.setStoppingRenderScript(true)
        binding.turtleCanvasView.invalidate()
    }

    private fun resumeCurrentScript() {
        Timber.tag(TAG).d("Execute Resume")
        binding.turtleCanvasView.setStoppingRenderScript(false)
        binding.turtleCanvasView.invalidate()
        renderState = RenderState.RUNNING
        requireActivity().invalidateOptionsMenu()
    }

    private fun stopCurrentScript() {
        Timber.tag(TAG).d("Execute Stop")
        binding.turtleCanvasView.setStoppingRenderScript(true)
    }

    private val onCanvasRenderStartedListener = {
        Timber.tag(TAG).d("Render Started Listener")
        if (evaluatorState != EvaluatorState.NONE) {
            renderState = RenderState.RUNNING
            requireActivity().invalidateOptionsMenu()
        }

        binding.executionBar.visibility = View.GONE
    }

    private val onCanvasRenderStoppedListener = {
        Timber.tag(TAG).d("Render Stopped Listener")
        if (evaluatorState != EvaluatorState.NONE) {
            renderState = RenderState.STOPPED
            requireActivity().invalidateOptionsMenu()
        }
    }

    private val onCanvasRenderFinishedListener = {
        Timber.tag(TAG).d("Render Finished Listener")
        if (evaluatorState != EvaluatorState.NONE) {
            renderState = RenderState.FINISHED
            requireActivity().invalidateOptionsMenu()
        }

        binding.executionBar.visibility = View.VISIBLE
        binding.executionBar.max = binding.turtleCanvasView.getInstructionsSize()
        binding.executionBar.progress = binding.executionBar.max
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