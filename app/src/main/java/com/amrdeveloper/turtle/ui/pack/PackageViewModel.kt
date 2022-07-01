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

package com.amrdeveloper.turtle.ui.pack

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.turtle.R
import com.amrdeveloper.turtle.data.LiloPackage
import com.amrdeveloper.turtle.data.source.LiloPackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "PackageViewModel"

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val liloPackageRepository: LiloPackageRepository
) : ViewModel() {

    private val _stateMessage = MutableLiveData<Int>()
    val stateMessage = _stateMessage

    private val _operationState = MutableLiveData(false)
    val operationState = _operationState

    fun savePackage(liloPackage: LiloPackage) {
        viewModelScope.launch {
            val result = liloPackageRepository.insertLiloPackage(liloPackage)
            if (result.isSuccess && result.getOrDefault(-1) > 0) {
                Timber.tag(TAG).d("New lilo package inserted")
                _stateMessage.value = R.string.package_inserted_success
                _operationState.value = true
            } else {
                Timber.tag(TAG).d("New lilo package not inserted because ${result.exceptionOrNull()?.message}")
                _stateMessage.value = R.string.package_inserted_errors
            }
        }
    }

    fun updatePackage(liloPackage: LiloPackage) {
        viewModelScope.launch {
            val result = liloPackageRepository.updateLiloPackage(liloPackage)
            if (result.isSuccess && result.getOrDefault(-1) > 0) {
                Timber.tag(TAG).d("Lilo package updated")
                _stateMessage.value = R.string.package_updated_success
                _operationState.value = true
            } else {
                Timber.tag(TAG).d("Lilo package not updated because ${result.exceptionOrNull()?.message}")
                _stateMessage.value = R.string.package_updated_errors
            }
        }
    }
}