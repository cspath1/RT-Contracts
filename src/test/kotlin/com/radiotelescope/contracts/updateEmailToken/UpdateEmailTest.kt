package com.radiotelescope.contracts.updateEmailToken

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

@DataJpaTest
@RunWith(SpringRunner::class)
internal class UpdateEmailTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    private lateinit var token: UpdateEmailToken
    private lateinit var user: User

    @Before
    fun init(){
        // Persist the user and token
        user = testUtil.createUser("rpim@ycp.edu")
        token = testUtil.createUpdateEmailToken(
                user = user,
                token = "SomeToken",
                email = "rpim1@ycp.edu"
        )
    }

    @Test
    fun testValid_CorrectConstraints_Success(){
        val (id, error) = UpdateEmail(
                token = token.token,
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        //Should have passed
        Assert.assertNotNull(id)
        Assert.assertNull(error)

        // Ensure the token record was deleted
        Assert.assertEquals(0, updateEmailTokenRepo.count())
    }



    @Test
    fun testInvalid_BlankToken_Failure(){
        val (id, error) = UpdateEmail(
                token = "",
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        Assert.assertNull(id)
        Assert.assertNotNull(error)

        // Ensure it failed because of the password
        Assert.assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }

    @Test
    fun testInvalid_TokenNotFound_Failure() {
        val (id, error) = UpdateEmail(
                token = "NotTOKENs",
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        Assert.assertNull(id)
        Assert.assertNotNull(error)

        // Ensure it failed because of the token
        Assert.assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }

    @Test
    fun testInvalid_TokenExpired_Failure() {
        // make the token expired
        token.expirationDate = Date(System.currentTimeMillis() - 100000L)
        updateEmailTokenRepo.save(token)

        val (id, error) = UpdateEmail(
                token = token.token,
                updateEmailTokenRepo = updateEmailTokenRepo,
                userRepo = userRepo
        ).execute()

        // Should have failed
        Assert.assertNull(id)
        Assert.assertNotNull(error)

        // Ensure it failed because of the token
        Assert.assertTrue(error!![ErrorTag.TOKEN].isNotEmpty())
    }

}