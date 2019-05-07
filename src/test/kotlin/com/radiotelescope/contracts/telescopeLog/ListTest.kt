package com.radiotelescope.contracts.telescopeLog

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.telescopeLog.ITelescopeLogRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeLog.sql"])
internal class ListTest : AbstractSpringTest() {
    @Autowired
    private lateinit var telescopeLogRepo: ITelescopeLogRepository

    private val pageable = PageRequest.of(0, 5)

    @Test
    fun testValidConstraints_Success() {
        val (page, errors) = List(
                pageable = pageable,
                telescopeLogRepo = telescopeLogRepo
        ).execute()

        assertNull(errors)
        assertNotNull(page)

        assertEquals(2, page!!.content.size)
    }
}