package com.radiotelescope.repository.updateEmailToken

import com.radiotelescope.AbstractSpringTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateEmailTokenTest : AbstractSpringTest() {
    @Autowired
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    private var token = "someToken"
    private var email = "rpim1@ycp.edu"

    @Before
    fun init() {
        testUtil.createUpdateEmailToken(
                user = testUtil.createUser("rpim@ycp.edu"),
                token = token,
                email = email
        )
    }

    @Test
    fun testExistsByToken() {
        val exists = updateEmailTokenRepo.existsByToken(token)

        Assert.assertTrue(exists)
    }

    @Test
    fun testFindByToken() {
        val theAccountActivateToken = updateEmailTokenRepo.findByToken(token)

        Assert.assertNotNull(theAccountActivateToken)
    }

}