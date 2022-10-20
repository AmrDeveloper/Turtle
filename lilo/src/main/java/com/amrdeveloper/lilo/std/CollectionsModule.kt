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

package com.amrdeveloper.lilo.std

import com.amrdeveloper.lilo.evaluator.LiloCallable
import com.amrdeveloper.lilo.evaluator.LiloEvaluator
import com.amrdeveloper.lilo.evaluator.LiloList
import com.amrdeveloper.lilo.evaluator.LiloScope

class CollectionsModule : LiloModule {

    private val collectionsLength = object : LiloCallable {

        override fun arity(): Int = 1

        override fun call(interpreter: LiloEvaluator, arguments: List<Any>): Any {
            if (arguments.first() is LiloList) {
                return (arguments.first() as LiloList).values.size.toFloat()
            }
            return 0.0f
        }
    }

    override fun bindModule(scope: LiloScope) {
        scope.define("len", collectionsLength)
    }
}