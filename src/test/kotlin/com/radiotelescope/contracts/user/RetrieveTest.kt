package com.radiotelescope.contracts.user

import com.radiotelescope.AbstractSpringTest
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : AbstractSpringTest() {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

    private var id: Long = 0


    @Before
    fun setUp() {
        // Instantiate and persist a User Entity Object and give them a time cap
        val user = testUtil.createUser("cspath1@ycp.edu")
        testUtil.createAllottedTimeCapForUser(user, 0L)
        id = user.id
    }

    @Test
    fun userExists(){
        //boolean for existById query
        val exists: Boolean = userRepo.existsById(id)
        //should be true because id is valid
        Assert.assertTrue(exists)
    }

    @Test
    fun validRetrieveTest(){
       val (info, error) = Retrieve(
               id = id,
               userRepo = userRepo,
               userRoleRepo = userRoleRepo,
               allottedTimeCapRepo = allottedTimeCapRepo
       ).execute()
        //error should be null, because id belongs to a valid user
        assertNull(error)
        assertNotNull(info)

    }

    @Test
    fun invalidRetrieveTest(){
        //executes retrieve method and passes return values into info and error
        val (info, error) = Retrieve(
                id = 311,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        ).execute()
        //info should be null because id 311 does not exist
        assertNull(info)
        assertNotNull(error)

    }
}