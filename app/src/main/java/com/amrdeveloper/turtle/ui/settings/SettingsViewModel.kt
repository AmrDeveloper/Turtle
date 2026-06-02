package com.amrdeveloper.turtle.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrdeveloper.turtle.ui.config.UIConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val uiConfig: UIConfig) : ViewModel() {

    val colorSchema : StateFlow<String> = uiConfig.selectedColorSchema
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = "Default"
        )

    fun setColorSchema(colorSchema : String) {
        viewModelScope.launch {
            uiConfig.setColorSchema(colorSchema)
        }
    }
}
