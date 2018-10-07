/*
 * Copyright (C) 2017 Yaroslav Mytkalyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doctoror.particleswallpaper.userprefs.data

import android.content.SharedPreferences

open class InMemorySharedPreferences : SharedPreferences {

    private var listeners = listOf<SharedPreferences.OnSharedPreferenceChangeListener?>()

    private val map = mutableMapOf<String?, Any?>()

    override fun contains(key: String?) = map.containsKey(key)

    override fun getBoolean(key: String?, defValue: Boolean) = map[key] as Boolean? ?: defValue

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?
    ) {
        listeners
            .filter { it === listener }
            .forEach { listeners = listeners.minus(it) }
    }

    override fun getInt(key: String?, defValue: Int) = map[key] as Int? ?: defValue

    override fun getAll() = map

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun getLong(key: String?, defValue: Long) = map[key] as Long? ?: defValue

    override fun getFloat(key: String?, defValue: Float) = map[key] as Float? ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(key: String?, defValues: MutableSet<String>?) =
        map[key] as MutableSet<String>?
            ?: defValues

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        if (listener != null && !containsListener(listener)) {
            listeners = listeners.plus(listener)
        }
    }

    override fun getString(key: String?, defValue: String?) = map[key] as String? ?: defValue

    private fun containsListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) =
        listeners.any { it === listener }

    private fun putValueAndNotify(key: String?, value: Any?) {
        map[key] = value
        notifyItemChanged(key)
    }

    private fun notifyItemChanged(key: String?) {
        for (item in listeners) {
            item?.onSharedPreferenceChanged(this, key)
        }
    }

    private inner class EditorImpl : SharedPreferences.Editor {

        private var clear = false
        private val map = mutableMapOf<String?, Any>()

        override fun clear(): SharedPreferences.Editor {
            clear = true
            return this
        }

        override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor =
            TODO("not implemented")

        override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            map[key] = values as Any
            return this
        }

        override fun commit(): Boolean {
            doCommit()
            return true
        }

        override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun apply() = doCommit()

        override fun putString(key: String?, value: String?): SharedPreferences.Editor {
            map[key] = value as Any
            return this
        }

        private fun doCommit() {
            if (clear) {
                this@InMemorySharedPreferences.map.clear()
            }
            for ((key, value) in map) {
                putValueAndNotify(key, value)
            }
        }
    }
}
