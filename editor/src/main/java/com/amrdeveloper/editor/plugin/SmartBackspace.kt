package com.amrdeveloper.editor.plugin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.delete
import com.amrdeveloper.editor.constant.tokenPairs

@OptIn(ExperimentalFoundationApi::class)
val smartBackspaceTransformation = InputTransformation {
    // Detect if a single character was deleted (Backspace)
    if (changes.changeCount == 1 && changes.getRange(0).length == 0 && changes.getOriginalRange(0).length == 1) {
        val deletePos = changes.getRange(0).min
        if (deletePos < length) {
            val charBefore = originalText[deletePos]
            val charAfter = asCharSequence()[deletePos]

            // If we deleted an opening bracket and the next char is its closing pair, delete it too
            if (tokenPairs[charBefore] == charAfter) {
                delete(deletePos, deletePos + 1)
            }
        }
    }
}
