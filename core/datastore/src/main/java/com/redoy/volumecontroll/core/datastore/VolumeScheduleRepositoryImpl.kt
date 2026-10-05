package com.redoy.volumecontroll.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.redoy.volumecontroll.core.domain.model.VolumeSchedule
import com.redoy.volumecontroll.core.domain.repository.VolumeScheduleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

private val Context.scheduleDatastore by preferencesDataStore(name = "volume_schedule_preferences")

@Singleton
class VolumeScheduleRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VolumeScheduleRepository {

    private object Keys {
        val SCHEDULES_KEY = stringPreferencesKey("volume_schedules_json")
    }

    override val schedules: Flow<List<VolumeSchedule>> = context.scheduleDatastore.data
        .map { prefs ->
            val json = prefs[Keys.SCHEDULES_KEY] ?: ""
            if (json.isEmpty()) {
                listOf(
                    VolumeSchedule("1", "Silent", 22, 0, true),
                    VolumeSchedule("2", "Normal", 8, 30, true)
                )
            } else {
                try {
                    val array = JSONArray(json)
                    val list = mutableListOf<VolumeSchedule>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            VolumeSchedule(
                                id = obj.getString("id"),
                                profileName = obj.getString("profileName"),
                                hour = obj.getInt("hour"),
                                minute = obj.getInt("minute"),
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

    override suspend fun saveSchedule(schedule: VolumeSchedule) {
        context.scheduleDatastore.edit { prefs ->
            val json = prefs[Keys.SCHEDULES_KEY] ?: ""
            val list = mutableListOf<VolumeSchedule>()
            if (json.isNotEmpty()) {
                try {
                    val array = JSONArray(json)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        list.add(
                            VolumeSchedule(
                                id = obj.getString("id"),
                                profileName = obj.getString("profileName"),
                                hour = obj.getInt("hour"),
                                minute = obj.getInt("minute"),
                                isEnabled = obj.getBoolean("isEnabled")
                            )
                        )
                    }
                } catch (_: Exception) {}
            }

            val index = list.indexOfFirst { it.id == schedule.id }
            if (index >= 0) {
                list[index] = schedule
            } else {
                list.add(schedule)
            }

            val newArray = JSONArray()
            for (s in list) {
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("profileName", s.profileName)
                    put("hour", s.hour)
                    put("minute", s.minute)
                    put("isEnabled", s.isEnabled)
                }
                newArray.put(obj)
            }
            prefs[Keys.SCHEDULES_KEY] = newArray.toString()
        }
    }

    override suspend fun deleteSchedule(id: String) {
        context.scheduleDatastore.edit { prefs ->
            val json = prefs[Keys.SCHEDULES_KEY] ?: ""
            if (json.isEmpty()) return@edit
            val list = mutableListOf<VolumeSchedule>()
            try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        VolumeSchedule(
                            id = obj.getString("id"),
                            profileName = obj.getString("profileName"),
                            hour = obj.getInt("hour"),
                            minute = obj.getInt("minute"),
                            isEnabled = obj.getBoolean("isEnabled")
                        )
                    )
                }
            } catch (_: Exception) {}

            list.removeAll { it.id == id }

            val newArray = JSONArray()
            for (s in list) {
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("profileName", s.profileName)
                    put("hour", s.hour)
                    put("minute", s.minute)
                    put("isEnabled", s.isEnabled)
                }
                newArray.put(obj)
            }
            prefs[Keys.SCHEDULES_KEY] = newArray.toString()
        }
    }
}
