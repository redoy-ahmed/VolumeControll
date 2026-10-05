package com.redoy.volumecontroll.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.redoy.volumecontroll.core.domain.model.AppVolumeRule
import com.redoy.volumecontroll.core.domain.repository.PerAppVolumeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.perAppDatastore by preferencesDataStore(name = "per_app_volume_preferences")

@Singleton
class PerAppVolumeRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    PerAppVolumeRepository {

    private object Keys {
        val RULES_KEY = stringPreferencesKey("app_volume_rules_json")
    }

    override val volumeRules: Flow<List<AppVolumeRule>> = context.perAppDatastore.data
        .map { prefs ->
            val json = prefs[Keys.RULES_KEY] ?: ""
            if (json.isEmpty()) {
                listOf(
                    AppVolumeRule("com.spotify.music", "Spotify", 8, true),
                    AppVolumeRule("com.google.android.youtube", "YouTube", 5, true)
                )
            } else {
                try {
                    val array = JSONArray(json)
                    val list = mutableListOf<AppVolumeRule>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            AppVolumeRule(
                                packageName = obj.getString("packageName"),
                                appName = obj.getString("appName"),
                                targetVolume = obj.getInt("targetVolume"),
                                isEnabled = obj.getBoolean("isEnabled")
                            )
                        )
                    }
                    list
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }

    override suspend fun saveRule(rule: AppVolumeRule) {
        context.perAppDatastore.edit { prefs ->
            val json = prefs[Keys.RULES_KEY] ?: ""
            val list = mutableListOf<AppVolumeRule>()
            if (json.isNotEmpty()) {
                try {
                    val array = JSONArray(json)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            AppVolumeRule(
                                packageName = obj.getString("packageName"),
                                appName = obj.getString("appName"),
                                targetVolume = obj.getInt("targetVolume"),
                                isEnabled = obj.getBoolean("isEnabled")
                            )
                        )
                    }
                } catch (_: Exception) {
                }
            }

            val index = list.indexOfFirst { it.packageName == rule.packageName }
            if (index >= 0) {
                list[index] = rule
            } else {
                list.add(rule)
            }

            val newArray = JSONArray()
            for (r in list) {
                val obj = JSONObject().apply {
                    put("packageName", r.packageName)
                    put("appName", r.appName)
                    put("targetVolume", r.targetVolume)
                    put("isEnabled", r.isEnabled)
                }
                newArray.put(obj)
            }
            prefs[Keys.RULES_KEY] = newArray.toString()
        }
    }

    override suspend fun deleteRule(packageName: String) {
        context.perAppDatastore.edit { prefs ->
            val json = prefs[Keys.RULES_KEY] ?: ""
            if (json.isEmpty()) return@edit
            val list = mutableListOf<AppVolumeRule>()
            try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        AppVolumeRule(
                            packageName = obj.getString("packageName"),
                            appName = obj.getString("appName"),
                            targetVolume = obj.getInt("targetVolume"),
                            isEnabled = obj.getBoolean("isEnabled")
                        )
                    )
                }
            } catch (_: Exception) {
            }

            list.removeAll { it.packageName == packageName }

            val newArray = JSONArray()
            for (r in list) {
                val obj = JSONObject().apply {
                    put("packageName", r.packageName)
                    put("appName", r.appName)
                    put("targetVolume", r.targetVolume)
                    put("isEnabled", r.isEnabled)
                }
                newArray.put(obj)
            }
            prefs[Keys.RULES_KEY] = newArray.toString()
        }
    }
}