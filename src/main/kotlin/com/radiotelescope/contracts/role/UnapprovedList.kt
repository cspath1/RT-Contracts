package com.radiotelescope.contracts.role

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.contracts.user.UserInfo
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

/**
 * Implementation of the [Command] interface used to find [UserRole] objects that
 * need an admin's approval
 *
 * @param pageable the [Pageable] interface
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 */
class UnapprovedList(
        private val pageable: Pageable,
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository
) : Command<Page<UserRoleInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that will grab any [UserRole] objects
     * that need approved by an admin and adapts them into a [UserRoleInfo] before
     * returning a [Page] to the client.
     */
    override fun execute(): SimpleResult<Page<UserRoleInfo>, Multimap<ErrorTag, String>> {
        val unapprovedRoles = userRoleRepo.findNeedsApprovedUserRoles(pageable)

        val roleInfos = arrayListOf<UserRoleInfo>()
        unapprovedRoles.forEach {
            if (it.userId != null) {
                // Since this is used to retrieve an unapproved role, the userRoleLabel field will
                // always be null
                val userInfo = UserInfo(userRepo.findById(it.userId!!).get(), null)

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