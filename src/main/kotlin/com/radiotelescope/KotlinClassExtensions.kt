package com.radiotelescope

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.user.User
import com.radiotelescope.security.AccessReport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

fun <T: Enum<T>> Multimap<T, String>.toStringMap(): Map<String, Collection<String>> {
    return this.asMap().mapKeys { it.key.name }
}

fun AccessReport.toStringMap(): Map<String, Collection<String>> {
    val map = kotlin.collections.mutableMapOf<String, Collection<String>>()
    map["MISSING_ROLES"] = this.missingRoles.map { it.name }
    return map
}

fun Page<User>.toUserInfoPage(): Page<UserInfo> {
    val infoList = arrayListOf<UserInfo>()
    forEach {
        infoList.add(UserInfo(it))
    }

    return PageImpl<UserInfo>(infoList, pageable, totalElements)
}