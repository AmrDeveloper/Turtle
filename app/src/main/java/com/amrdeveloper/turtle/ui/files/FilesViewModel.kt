package com.amrdeveloper.turtle.ui.files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.turtle.data.LiloFileEntity
import com.amrdeveloper.turtle.data.LiloFileRepository
import com.amrdeveloper.turtle.common.LazyValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val fileRepository: LiloFileRepository
) : ViewModel() {

    val uiState: StateFlow<LazyValue<List<LiloFileEntity>>> = fileRepository.getLiloFiles()
        .map { LazyValue(data = it, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
            initialValue = LazyValue(data = listOf(), isLoading = true)
        )
}
