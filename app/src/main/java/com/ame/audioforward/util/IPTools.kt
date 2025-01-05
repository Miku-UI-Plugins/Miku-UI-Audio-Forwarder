package com.ame.audioforward.util

import java.util.regex.Pattern

class IPTools {
    companion object {
        fun isValidIpv4Address(ip: String): Boolean {
            if (ip.isBlank() || ip.length > 15) return false
            val pattern =
                Pattern.compile(
                    "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.)" +
                        "{3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$",
                )
            val matcher = pattern.matcher(ip)
            return matcher.matches()
        }
    }
}
