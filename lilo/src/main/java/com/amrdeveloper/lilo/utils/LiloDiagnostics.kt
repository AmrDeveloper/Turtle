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

package com.amrdeveloper.lilo.utils

import com.amrdeveloper.lilo.front.TokenPosition

class LiloDiagnostics {

    private val errorDiagnostics = mutableListOf<Diagnostic>()
    private val warnsDiagnostics = mutableListOf<Diagnostic>()

    fun reportError(position: TokenPosition, message : String) {
        errorDiagnostics.add(Diagnostic(position, message, DiagnosticType.ERROR))
    }

    fun reportWarn(position: TokenPosition, message : String) {
        warnsDiagnostics.add(Diagnostic(position, message, DiagnosticType.WARN))
    }

    fun errorDiagnostics() : List<Diagnostic> = errorDiagnostics

    fun warnsDiagnostics() : List<Diagnostic> = warnsDiagnostics

    fun clearErrors() {
        errorDiagnostics.clear()
    }

    fun clearWarns() {
        warnsDiagnostics.clear()
    }

    fun errorNumber() : Int = errorDiagnostics.size

    fun warnsNumber() : Int = warnsDiagnostics.size
}