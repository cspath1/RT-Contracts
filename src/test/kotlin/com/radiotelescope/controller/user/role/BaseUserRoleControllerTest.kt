package com.radiotelescope.controller.user

import com.radiotelescope.contracts.role.BaseUserRoleFactory
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.controller.BaseRestControllerTest
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseUserRoleControllerTest : BaseRestControllerTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    // These will both be needed in all user rest controller
    // so instantiate them here
    private lateinit var wrapper: UserUserRoleWrapper
    private lateinit var factory: BaseUserRoleFactory

    @Before
    override fun init() {
        super.init()

        factory = BaseUserRoleFactory(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )

        wrapper = UserUserRoleWrapper(
                context = getContext(),
                factory = factory,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        )
    }

    // Once instantiated, this will not be altered
    // so only supply a getter for it
    fun getWrapper(): UserUserRoleWrapper {
        return wrapper
    }
}