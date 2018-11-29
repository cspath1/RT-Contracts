package com.radiotelescope.controller

import com.radiotelescope.controller.spring.Logger
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.security.FakeUserContext
import com.radiotelescope.security.UserContext
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
abstract class BaseRestControllerTest {
    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    private lateinit var logger: Logger
    private val context = FakeUserContext()

    @Before
    fun init() {
        logger = Logger(
                logRepo = logRepo,
                errorRepo = errorRepo,
                userContext = context
        )
    }

    fun getLogger(): Logger {
        return logger
    }

    fun getContext(): UserContext {
        return context
    }
}