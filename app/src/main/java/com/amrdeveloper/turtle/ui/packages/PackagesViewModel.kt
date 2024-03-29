package com.amrdeveloper.turtle.ui.packages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.turtle.data.LiloPackage
import com.amrdeveloper.turtle.data.source.LiloPackageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val liloPackageRepository: LiloPackageRepository
) : ViewModel() {

    private val _liloPackagesLiveData = MutableLiveData<List<LiloPackage>>()
    val liloPackagesLiveData = _liloPackagesLiveData

    fun loadLiloPackages() {
        viewModelScope.launch {
            val result = liloPackageRepository.loadLiloPackages()
            if (result.isSuccess) {
                _liloPackagesLiveData.value = result.getOrDefault(listOf())
            }
        }
    }

    fun loadLiloPackagesByKeyword(keyword : String) {
        viewModelScope.launch {
            val result = liloPackageRepository.loadLiloPackagesByKeyword(keyword)
            if (result.isSuccess) {
                _liloPackagesLiveData.value = result.getOrDefault(listOf())
            }
        }
    }

    fun insertLiloPackage(liloPackage: LiloPackage) {
        viewModelScope.launch {
            liloPackageRepository.insertLiloPackage(liloPackage)
        }
    }

    fun deleteLiloPackage(liloPackage : LiloPackage) {
        viewModelScope.launch {
            val result = liloPackageRepository.deleteLiloPackage(liloPackage)
            if (result.isSuccess) {
                liloPackagesLiveData.value?.toMutableList()?.remove(liloPackage)
            }
        }
    }
}