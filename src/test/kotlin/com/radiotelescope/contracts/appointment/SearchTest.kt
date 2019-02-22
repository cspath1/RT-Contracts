package com.radiotelescope.contracts.appointment

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.model.appointment.Filter
import com.radiotelescope.repository.model.appointment.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import liquibase.integration.spring.SpringLiquibase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = ["classpath:sql/seedTelescope.sql"])
@ActiveProfiles(value = ["test"])
internal class SearchTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }

        @Bean
        fun liquibase(): SpringLiquibase {
            val liquibase = SpringLiquibase()
            liquibase.setShouldRun(false)
            return liquibase
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var userRepo: IUserRepository

    private lateinit var pageable: Pageable

    private lateinit var user: User
    private lateinit var userTwo: User

    @Before
    fun setUp() {
        // Create two users
        user = testUtil.createUser("cspath1@ycp.edu")
        user.firstName = "Cody"
        user.lastName = "Spath"
        userRepo.save(user)

        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )

        userTwo = testUtil.createUser("spathca@gmail.com")
        userTwo.firstName = "Charles"
        userTwo.lastName = "Spath"
        userRepo.save(userTwo)

        testUtil.createUserRolesForUser(
                user = userTwo,
                role = UserRole.Role.GUEST,
                isApproved = true
        )

        // Create a scheduled and completed appointment for each user
        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 100000L),
                endTime = Date(System.currentTimeMillis() + 200000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = user,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 200000L),
                endTime = Date(System.currentTimeMillis() - 100000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = userTwo,
                telescopeId = 1L,
                status = Appointment.Status.SCHEDULED,
                startTime = Date(System.currentTimeMillis() + 300000L),
                endTime = Date(System.currentTimeMillis() + 400000L),
                isPublic = true
        )

        testUtil.createAppointment(
                user = userTwo,
                telescopeId = 1L,
                status = Appointment.Status.COMPLETED,
                startTime = Date(System.currentTimeMillis() - 400000L),
                endTime = Date(System.currentTimeMillis() - 300000L),
                isPublic = true
        )

        pageable = PageRequest.of(0, 25)
    }

    @Test
    fun testValidConstraints_FullName_Success() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_FULL_NAME, "Cody Spath"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have two AppointmentInfo objects
        assertEquals(2, page!!.content.size)

        page.forEach {
            assertEquals(user.id, it.userId)
        }
    }

    @Test
    fun testValidConstraints_LastNameOrEmail_Success() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_EMAIL, "spath"))
        searchCriteria.add(SearchCriteria(Filter.USER_LAST_NAME, "spath"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have four AppointmentInfo objects
        assertEquals(4, page!!.content.size)

        page.forEach {
            assertTrue(it.userFirstName.toLowerCase().contains("spath") ||
                    it.userEmail.toLowerCase().contains("spath"))
        }
    }

    @Test
    fun testFullName_InvalidSearchValue_Failure() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_FULL_NAME, "cody"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

    @Test
    fun testNoSearchParams_Failure() {
        val searchCriteria = arrayListOf<SearchCriteria>()

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

    @Test
    fun testIncompatibleSearchParams_Failure() {
        val searchCriteria = arrayListOf<SearchCriteria>()
        searchCriteria.add(SearchCriteria(Filter.USER_FULL_NAME, "Cody Spath"))
        searchCriteria.add(SearchCriteria(Filter.USER_EMAIL, "ycp.edu"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                appointmentRepo = appointmentRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }
}