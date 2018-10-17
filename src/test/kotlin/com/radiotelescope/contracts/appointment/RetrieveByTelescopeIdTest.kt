package com.radiotelescope.contracts.appointment

import com.google.common.collect.HashMultimap
import com.radiotelescope.repository.appointment.Appointment
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import com.google.common.collect.Multimap
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.telescope.Telescope
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.util.*


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescopeForRetrieveByTelescopeIdTest.sql"])
internal class RetrieveByTelescopeIdTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private var user_id: Long = 0

    @Before
    fun setUp() {
        // Persist a user
        val user = testUtil.createUser("spathcody@gmail.com")
        //count tells you how many telescopes are in the Repo
        assertEquals(1, telescopeRepo.count())
        user_id = user.id

    }

    @Test
    fun retrieveByTelescopeIdTest() {

        var telescope = telescopeRepo.findById(2)
        if (RetrieveByTelescopeId(appointmentRepo, telescope.get().getId(), PageRequest.of(0, 10), userRepo, user_id, telescopeRepo).execute().success == null)
            fail()
    }

    @Test
    fun invalidTelescopeId()
    {
       if (RetrieveByTelescopeId(appointmentRepo, -600, PageRequest.of(0, 10), userRepo, user_id, telescopeRepo).execute().error == null)
           fail()
    }

    @Test
    fun invalidUserId()
    {
        var telescope = telescopeRepo.findById(2)
        if (RetrieveByTelescopeId(appointmentRepo, telescope.get().getId(), PageRequest.of(0, 10), userRepo, -700, telescopeRepo).execute().error == null)
            fail()
    }

}