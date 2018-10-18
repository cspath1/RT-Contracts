package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toUserInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Override of the [Command] interface method used for User List retrieval
 *
 * @param pageable the [Pageable] interface
 * @param userRepo the [IUserRepository] interface
 */
class List(
        private val pageable: Pageable,
        private val userRepo: IUserRepository
) : Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
    /**
     * Override of the [Command.execute] method that calls the [IUserRepository.findAll]
     * method and passes in the pageable parameter. It then adapts the [Page] of Users
     * into a [Page] of [UserInfo] objects
     */
    override fun execute(): SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>> {
        val userPage = userRepo.findAll(pageable)
        return SimpleResult(userPage.toUserInfoPage(), null)
    }
}