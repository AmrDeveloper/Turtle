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

package com.amrdeveloper.lilo.evaluator

class LiloScope(private val enclosing: LiloScope? = null) {

    private val values = mutableMapOf<String, Any>()

    fun define(name : String, value : Any) {
        values[name] = value
    }

    fun ancestor(level: Int): LiloScope {
        var environment = this
        repeat(level) { environment.enclosing?.let { environment = it } }
        return environment
    }

    fun assign(name : String, value : Any) : Boolean {
        if (values.containsKey(name)) {
            values[name] = value
            return true
        }

        if (enclosing != null) {
            return enclosing.assign(name, value)
        }

        return false
    }

    fun assignAt(level : Int, name : String, value : Any) : Boolean {
        return ancestor(level).assign(name, value)
    }

    fun lookup(name : String) : Any? {
        if (values.containsKey(name)) {
            return values[name]
        }

        if (enclosing != null) {
            return enclosing.lookup(name)
        }

        return null
    }

    fun lookupAt(level : Int, name : String) : Any? {
        return ancestor(level).lookup(name)
    }
}