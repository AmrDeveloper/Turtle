package com.amrdeveloper.turtle.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.turtle.common.LazyValue
import com.amrdeveloper.turtle.data.LiloFileEntity
import com.amrdeveloper.turtle.data.LiloFileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SearchParams(
    var query: String = "",
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val fileRepository: LiloFileRepository
) : ViewModel() {

    private val searchParams = MutableStateFlow(value = SearchParams())

    @OptIn(ExperimentalCoroutinesApi::class)
    val sortedLiloFilesState: StateFlow<LazyValue<List<LiloFileEntity>>> =
        combine(searchParams) {
            searchParams.value
        }.flatMapLatest { params ->
            fileRepository.getLiloFiles(keyword = params.query)
        }.map {
            LazyValue(data = it, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LazyValue(data = emptyList(), isLoading = true)
        )

    fun updateSearchParams(params: SearchParams) {
        searchParams.value = params
    }
}
