package com.radiotelescope.contracts.log

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.model.log.Filter
import com.radiotelescope.repository.model.log.SearchCriteria
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class SearchTest : AbstractSpringTest() {
    @Autowired
    private lateinit var logRepo: ILogRepository

    private lateinit var pageable: Pageable

    private lateinit var user: User


    private lateinit var log1: Log
    private lateinit var log2: Log

    @Before
    fun setUp() {
        // Create a user
        user = testUtil.createUser("rpim@ycp.edu")
        testUtil.createUserRolesForUser(
                user = user,
                role = UserRole.Role.MEMBER,
                isApproved = true
        )

        log1 = testUtil.createLog(
                user = user,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.USER,
                action = "Testing",
                isSuccess = true,
                timestamp = Date(System.currentTimeMillis())
        )

        log2 = testUtil.createLog(
                user = user,
                affectedRecordId = 1L,
                affectedTable = Log.AffectedTable.FEEDBACK,
                action = "something else",
                isSuccess = false,
                timestamp = Date(System.currentTimeMillis())
        )
        log2.status = HttpStatus.FORBIDDEN.value()

        pageable = PageRequest.of(0, 25)

    }

    @Test
    fun testValidConstraints_AffectedTable_Success(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.AFFECTED_TABLE, Log.AffectedTable.USER),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have one LogInfo object
        assertEquals(1, page!!.content.size)

        assertEquals(log1.id, page.content[0].id)
    }

    @Test
    fun testValidConstraints_IsSuccess_Success(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.IS_SUCCESS, true),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have one LogInfo object
        assertEquals(1, page!!.content.size)

        assertEquals(log1.id, page.content[0].id)
    }

    @Test
    fun testValidConstraints_Status_Success(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.STATUS, HttpStatus.OK.value()),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have one LogInfo object
        assertEquals(1, page!!.content.size)

        assertEquals(log1.id, page.content[0].id)
    }

    @Test
    fun testValidConstraints_Action_Success(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.ACTION, "test"),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should not have failed
        assertNull(errors)
        assertNotNull(page)

        // Should have one LogInfo object
        assertEquals(1, page!!.content.size)

        assertEquals(log1.id, page.content[0].id)
    }

    @Test
    fun testInvalidValue_AffectedTable_Failure(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.AFFECTED_TABLE, 1),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

    @Test
    fun testInvalidValue_IsSuccess_Failure(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.IS_SUCCESS, "something"),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

    @Test
    fun testInvalidValue_Status_Failure(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.STATUS, "something"),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

    @Test
    fun testInvalidValue_Action_Failure(){
        val (page, errors) = Search(
                searchCriteria = SearchCriteria(Filter.ACTION, 1),
                pageable = pageable,
                logRepo = logRepo
        ).execute()

        // Should have failed
        assertNotNull(errors)
        assertNull(page)

        // Ensure it failed for the expected reason
        assertEquals(1, errors!!.size())
        assertTrue(errors[ErrorTag.SEARCH].isNotEmpty())
    }

}