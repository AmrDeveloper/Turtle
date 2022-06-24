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

package com.amrdeveloper.turtle.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amrdeveloper.lilo.Diagnostic
import com.amrdeveloper.lilo.LiloDiagnostics
import com.amrdeveloper.lilo.LiloParser
import com.amrdeveloper.lilo.LiloTokenizer
import com.amrdeveloper.lilo.ast.LiloScript

class MainViewModel : ViewModel() {

    private val _diagnosticsLiveData = MutableLiveData<List<Diagnostic>>()
    val diagnosticsLiveData = _diagnosticsLiveData

    private val _liloScript = MutableLiveData<LiloScript>()
    val liloScript = _liloScript

    private val _previewLiveData = MutableLiveData<Boolean>()
    val previewLiveData = _previewLiveData

    fun executeLiloScript(script : String) {
        val tokenizer = LiloTokenizer(script)
        val diagnostics = LiloDiagnostics()
        val parser = LiloParser(tokenizer.scanTokens(), diagnostics)
        val liloScript = parser.parseScript()
        if (diagnostics.errorNumber() > 0) {
            _diagnosticsLiveData.value = diagnostics.errorDiagnostics()
            return
        }
        _diagnosticsLiveData.value = listOf()
        _previewLiveData.value = true
        _liloScript.value = liloScript
    }
}