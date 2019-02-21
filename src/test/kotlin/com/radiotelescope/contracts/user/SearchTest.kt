package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.model.user.Filter
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
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
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
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
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private var searchCriteria = arrayListOf<SearchCriteria>()
    private lateinit var pageable: Pageable

    @Before
    fun setUp() {
        // Persist a few users and set them up
        val user = testUtil.createUser(
                email = "cspath1@ycp.edu",
                accountHash = "Test Account 1"
        )
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )
        user.firstName = "Cody"
        user.lastName = "Spath"
        user.company = "York College of PA"
        userRepo.save(user)

        val userTwo = testUtil.createUser(
                email = "rpim@ycp.edu",
                accountHash = "Test Account 2"
        )
        testUtil.createUserRolesForUser(
                user = userTwo,
                role = UserRole.Role.GUEST,
                isApproved = true
        )
        userTwo.firstName = "Rathana"
        userTwo.lastName = "Pim"
        userTwo.company = "York College of Pennsylvania"
        userRepo.save(userTwo)

        val userThree = testUtil.createUser(
                email = "spathca@gmail.com",
                accountHash = "Test Account 3"
        )
        testUtil.createUserRolesForUser(
                user= userThree,
                role = UserRole.Role.RESEARCHER,
                isApproved = true
        )
        userThree.firstName = "Charles"
        userThree.lastName = "Spath"
        userThree.company = "National Institute of Health"
        userRepo.save(userThree)

        // Determine some search criteria
        val searchCriteriaOne = SearchCriteria(Filter.FIRST_NAME, "Spath")
        val searchCriteriaTwo = SearchCriteria(Filter.LAST_NAME, "Spath")

        // Add them to the list
        searchCriteria.add(searchCriteriaOne)
        searchCriteria.add(searchCriteriaTwo)

        pageable = PageRequest.of(0, 25)
    }

    @Test
    fun testValidConstraints_FirstOrLastName_Success() {
        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have two UserInfo objects
        assertEquals(2, page!!.content.size)

        page.content.forEach {
            val correctResult = (it.firstName == "Spath") || (it.lastName == "Spath")
            assertTrue(correctResult)
        }
    }

    @Test
    fun testValidConstraints_Email_Success() {
        searchCriteria = arrayListOf()
        searchCriteria.add(SearchCriteria(Filter.EMAIL, "ycp.edu"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have 3 UserInfo objects
        assertEquals(2, page!!.content.size)

        page.content.forEach {
            assertTrue(it.email.contains("ycp.edu"))
        }
    }

    @Test
    fun testValidConstraints_Company_Success() {
        searchCriteria = arrayListOf()
        searchCriteria.add(SearchCriteria(Filter.COMPANY, "York College"))

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have two UserInfo objects
        assertEquals(2, page!!.content.size)

        page.content.forEach {
            assertNotNull(it.company)
            assertTrue(it.company!!.contains("York College"))
        }
    }

    @Test
    fun testNoSearchCriteria_Failure() {
        searchCriteria = arrayListOf()

        val (page, errors) = Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Should have failed due to no search parameters
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }
}