package com.radiotelescope.contracts.user

import com.radiotelescope.BaseDataJpaTest
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class ListTest : BaseDataJpaTest() {

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    private var pageable = PageRequest.of(0, 5)

    @Before
    fun setUp() {
        // Create a few user's
        testUtil.createUser("cspath1@ycp.edu")
        testUtil.createUser("spathcody@gmail.com")
        testUtil.createUser("codyspath@gmail.com")
    }

    @Test
    fun testPopulatedRepo_Success() {
        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo
        ).execute()

        Assert.assertNull(errors)
        Assert.assertNotNull(page)
        Assert.assertEquals(3, page!!.content.size)
    }

    @Test
    fun testEmptyRepo_Success() {
        userRepo.deleteAll()

        val (page, errors) = List(
                pageable = pageable,
                userRepo = userRepo
        ).execute()

        Assert.assertNotNull(page)
        Assert.assertNull(errors)
        Assert.assertEquals(0, page!!.content.size)
    }
}