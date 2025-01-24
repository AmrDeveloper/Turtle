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

package com.amrdeveloper.turtle.data

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.amrdeveloper.easyadapter.adapter.ListAdapter
import com.amrdeveloper.easyadapter.bind.BindListener
import com.amrdeveloper.easyadapter.bind.BindText
import com.amrdeveloper.easyadapter.option.ListenerType
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
@BindListener(ListenerType.OnClick)
@BindListener(ListenerType.OnLongClick)
@ListAdapter("com.amrdeveloper.turtle", "list_item_package", "name")
@Entity(tableName = "lilo_package", indices = [Index(value = ["name"], unique = true)])
data class LiloPackage (
    @BindText("document_title_txt") var name: String,
    var sourceCode: String,
    var creationTimeStamp: Long = System.currentTimeMillis(),
    var updateTimeStamp: Long = -1,
    var isUpdated: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
) : Parcelable