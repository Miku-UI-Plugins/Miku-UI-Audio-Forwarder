package com.ame.audioforward.util

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

/**
 * Taken from MULS
 */
class SPUtil {
    companion object {
        private lateinit var context: Application

        private val sharedPreferences: SharedPreferences? by lazy {
            PreferenceManager.getDefaultSharedPreferences(context)
        }
        private val mEditor: SharedPreferences.Editor? by lazy { sharedPreferences?.edit() }

        fun init(application: Application) {
            context = application
        }

        fun put(
            key: String,
            value: Any,
            isCommit: Boolean = false,
        ) {
            when (value) {
                is Boolean -> mEditor?.putBoolean(key, value)
                is String -> mEditor?.putString(key, value)
                is Int -> mEditor?.putInt(key, value)
                is HashSet<*> -> {
                    val stringSet = HashSet<String>()
                    for (element in value) {
                        if (element is String) {
                            stringSet.add(element)
                            continue
                        }
                        throw IllegalStateException("Unsupported type: HashSet<${element.javaClass.name}>")
                    }
                    mEditor?.putStringSet(key, stringSet)
                }
                else -> throw IllegalStateException("Unsupported type: ${value.javaClass.name}")
            }
            if (isCommit) {
                mEditor?.commit()
            } else {
                mEditor?.apply()
            }
        }

        fun get(
            key: String,
            defValue: Boolean,
        ): Boolean = sharedPreferences?.getBoolean(key, defValue) ?: defValue

        fun get(
            key: String,
            defValue: String,
        ): String = sharedPreferences?.getString(key, defValue) ?: defValue

        fun get(
            key: String,
            defValue: Int,
        ): Int = sharedPreferences?.getInt(key, defValue) ?: defValue

        fun get(
            key: String,
            defValue: HashSet<String>,
        ): HashSet<String> = sharedPreferences?.getStringSet(key, defValue) as HashSet<String>
    }
}
