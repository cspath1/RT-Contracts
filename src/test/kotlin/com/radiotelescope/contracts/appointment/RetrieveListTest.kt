package com.radiotelescope.contracts.appointment

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class RetrieveListTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    private var user_id:Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        user_id = user.id

    }

    @Test
    fun retrieveListTest()
    {
        if ( RetrieveList(appointmentRepo, user_id, userRepo, PageRequest.of(0, 10)).execute().success  == null)
            fail()
    }
}