package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class UnapprovedList(
        private val pageable: Pageable,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Page<UserRoleInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<UserRoleInfo>, Multimap<ErrorTag, String>> {
        val unapprovedRoles = userRoleRepo.findNeedsApprovedUserRoles(pageable)

        val roleInfos = arrayListOf<UserRoleInfo>()
        unapprovedRoles.forEach {
            if (it.userId != null) {
                val userInfo = UserInfo(userRepo.findById(it.userId!!).get())
                roleInfos.add(UserRoleInfo(
                        userRole = it,
                        userInfo = userInfo
                ))
            }
        }

        val roleInfoPage: Page<UserRoleInfo> = PageImpl(roleInfos, pageable, unapprovedRoles.totalElements)

        return SimpleResult(
                success = roleInfoPage,
                error = null
        )
    }
}