package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.toUserInfoPage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class List(
        private val pageable: Pageable,
        private val userRepo: IUserRepository
) : Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
    override fun execute(): SimpleResult<Page<UserInfo>, Multimap<ErrorTag, String>> {
        val userPage = userRepo.findAll(pageable)
        return SimpleResult(userPage.toUserInfoPage(), null)
    }
}