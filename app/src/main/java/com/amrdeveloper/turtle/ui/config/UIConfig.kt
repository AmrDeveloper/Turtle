package com.amrdeveloper.turtle.ui.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

const val UI_CONFIG_PREFERENCE = "ui_config_preferences"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = UI_CONFIG_PREFERENCE)

class UIConfig(private val context: Context) {

    object UIConfigKeys {
        val COLOR_SCHEMA = stringPreferencesKey("color_schema")
    }

    val selectedColorSchema: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[UIConfigKeys.COLOR_SCHEMA] ?: "Default"
        }

    suspend fun setColorSchema(colorSchema : String) {
        context.dataStore.edit { preferences ->
            preferences[UIConfigKeys.COLOR_SCHEMA] = colorSchema
        }
    }
}
