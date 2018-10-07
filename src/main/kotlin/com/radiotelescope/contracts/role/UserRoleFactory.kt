package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRoleFactory {
    fun unapprovedList(pageable: Pageable): Command<Page<UserRoleInfo>, Multimap<ErrorTag, String>>
}