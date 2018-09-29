package com.radiotelescope

import com.google.common.collect.Multimap
import com.radiotelescope.security.AccessReport

fun <T: Enum<T>> Multimap<T, String>.toStringMap(): Map<String, Collection<String>> {
    return this.asMap().mapKeys { it.key.name }
}

fun AccessReport.toStringMap(): Map<String, Collection<String>> {
    val map = kotlin.collections.mutableMapOf<String, Collection<String>>()
    map["MISSING_ROLES"] = this.missingRoles.map { it.name }
    return map
}