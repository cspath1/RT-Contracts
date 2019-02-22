package com.radiotelescope.controller

import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.security.FakeUserContext
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
abstract class BaseRestControllerTest {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var logger: Logger

    // This is needed in each rest controller
    // so instantiate it here
    private val context = FakeUserContext()

    @Before
    fun init() {
        // This is needed in each rest controller
        // so instantiate it here
        logger = Logger(
                logRepo = logRepo,
                errorRepo = errorRepo,
                userRepo = userRepo,
                userContext = context
        )
    }

    // Once instantiated, this should be immutable
    // so only provide a getter for it
    fun getLogger(): Logger {
        return logger
    }

    // Once instantiated, this should be immutable
    // so only provide a getter for it
    final fun getContext(): FakeUserContext {
        return context
    }
}