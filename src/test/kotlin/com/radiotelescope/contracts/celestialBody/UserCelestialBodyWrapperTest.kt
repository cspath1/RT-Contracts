package com.radiotelescope.contracts.celestialBody

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.model.celestialBody.Filter
import com.radiotelescope.repository.model.celestialBody.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.security.FakeUserContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class UserCelestialBodyWrapperTest {
    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil { return TestUtil() }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    private val baseCreateRequest = Create.Request(
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    private val baseUpdateRequest = Update.Request(
            id = -1,
            name = "Crab Nebula",
            hours = 5,
            minutes = 34,
            seconds = 32,
            declination = 22.0
    )

    private var userId = -1L
    private var adminId = -1L
    private lateinit var celestialBody: CelestialBody

    private val context = FakeUserContext()
    private lateinit var factory: CelestialBodyFactory
    private lateinit var wrapper: UserCelestialBodyWrapper

    @Before
    fun init() {
        // Initialize the factory and wrapper
        factory = BaseCelestialBodyFactory(
                celestialBodyRepo = celestialBodyRepo,
                coordinateRepo = coordinateRepo
        )

        wrapper = UserCelestialBodyWrapper(
                context = context,
                factory = factory
        )

        // Persist a regular user
        val user = testUtil.createUser("spathcody@gmail.com")
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.STUDENT,
                isApproved = true
        )
        userId = user.id

        // Persist an admin user
        val admin = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUserRolesForUser(
                user = admin,
                role = UserRole.Role.ADMIN,
                isApproved = true
        )
        adminId = admin.id

        // Create a Celestial Body
        val coordinate = Coordinate(
                hours = 14,
                minutes = 39,
                seconds = 37,
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 14,
                        minutes = 39,
                        seconds = 37
                ),
                declination = -60.5
        )
        coordinateRepo.save(coordinate)

        celestialBody = testUtil.createCelestialBody(
                name = "Alpha Centausri",
                coordinate = coordinate
        )
    }

    @Test
    fun testCreate_Admin_Success() {
        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.create(
                request = baseCreateRequest
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testCreate_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.create(
                request = baseCreateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testCreate_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.create(
                request = baseCreateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_Admin_Success() {
        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.retrieve(celestialBody.id) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testRetrieve_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.retrieve(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testRetrieve_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.retrieve(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testList_Admin_Success() {
        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.list(PageRequest.of(0, 10)) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testList_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.list(PageRequest.of(0, 10)) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testList_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.list(PageRequest.of(0, 10)) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testMarkHidden_Admin_Success() {
        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val errors = wrapper.markHidden(celestialBody.id) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(errors)
    }

    @Test
    fun testMarkHidden_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.markHidden(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testMarkHidden_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.markHidden(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testMarkVisible_Admin_Success() {
        // Mark Celestial Body as hidden
        celestialBody.status = CelestialBody.Status.HIDDEN
        celestialBodyRepo.save(celestialBody)

        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val error = wrapper.markVisible(celestialBody.id) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testMarkVisible_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.STUDENT)

        val error = wrapper.markVisible(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testMarkVisible_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.markVisible(celestialBody.id) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testSearch_LoggedIn_Success() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.addAll(listOf(UserRole.Role.RESEARCHER, UserRole.Role.USER))

        val error = wrapper.search(
                searchCriteria = SearchCriteria(Filter.NAME, "Crab"),
                pageable = PageRequest.of(0, 15)
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testSearch_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.search(
                searchCriteria = SearchCriteria(Filter.NAME, "Crab"),
                pageable = PageRequest.of(0, 15)
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.USER))
    }

    @Test
    fun testUpdate_Admin_Success() {
        // Simulate a login
        context.login(adminId)
        context.currentRoles.add(UserRole.Role.ADMIN)

        val requestCopy = baseUpdateRequest.copy(
                id = celestialBody.id
        )

        val error = wrapper.update(
                request = requestCopy
        ) {
            assertNotNull(it.success)
            assertNull(it.error)
        }

        assertNull(error)
    }

    @Test
    fun testUpdate_NotAdmin_Failure() {
        // Simulate a login
        context.login(userId)
        context.currentRoles.add(UserRole.Role.GUEST)

        val error = wrapper.update(
                request = baseUpdateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }

    @Test
    fun testUpdate_NotLoggedIn_Failure() {
        // Simulate a logout
        context.logout()
        context.currentRoles = mutableListOf()

        val error = wrapper.update(
                request = baseUpdateRequest
        ) {
            fail("Should fail on precondition")
        }

        assertNotNull(error)
        assertTrue(error!!.missingRoles!!.contains(UserRole.Role.ADMIN))
    }
}