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

package com.amrdeveloper.turtle.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amrdeveloper.turtle.BuildConfig
import com.amrdeveloper.turtle.data.GITHUB_CONTRIBUTORS
import com.amrdeveloper.turtle.data.GITHUB_ISSUES
import com.amrdeveloper.turtle.data.GITHUB_SOURCE
import com.amrdeveloper.turtle.data.GOOGLE_PLAY_URL
import com.amrdeveloper.turtle.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setupSettingsListeners()
        return binding.root
    }

    private fun setupSettingsListeners() {
        binding.versionTxt.text = "Version ${BuildConfig.VERSION_NAME}"

        binding.sourceCodeTxt.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(GITHUB_SOURCE))
            startActivity(viewIntent)
        }

        binding.contributorsTxt.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(GITHUB_CONTRIBUTORS))
            startActivity(viewIntent)
        }

        binding.issuesTxt.setOnClickListener {
            val viewIntent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(GITHUB_ISSUES))
            startActivity(viewIntent)
        }

        binding.shareTxt.setOnClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, GOOGLE_PLAY_URL)
            sendIntent.type = "text/plain"
            val shareIndent = Intent.createChooser(sendIntent, null)
            startActivity(shareIndent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}