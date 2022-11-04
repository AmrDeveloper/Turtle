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

package com.amrdeveloper.turtle.util

import android.content.Context
import com.amrdeveloper.turtle.data.AppTheme

private const val SETTINGS_PREFERENCE_NAME = "turtle_preference"
private const val SETTINGS_THEME_KEY = "theme"
private val defaultTheme = AppTheme.DARK

class UserPreferences(private val context: Context) {

    fun setAppTheme(theme : AppTheme) {
        val preferences = context.getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(SETTINGS_THEME_KEY, theme.name)
        editor.apply()
    }

    fun getAppTheme() : AppTheme {
        val preferences = context.getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val themeName = preferences.getString(SETTINGS_THEME_KEY, defaultTheme.name) ?: defaultTheme.name
        return AppTheme.valueOf(themeName)
    }
}