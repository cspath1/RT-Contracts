package com.radiotelescope.contracts.user

import com.radiotelescope.BaseDataJpaTest
import com.radiotelescope.TestUtil
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import javax.validation.constraints.Null

@DataJpaTest
@RunWith(SpringRunner::class)
internal class RetrieveTest : BaseDataJpaTest() {
    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository
    private var id: Long = 0


    @Before
    fun setUp() {
        // Instantiate and persist a User Entity Object
        val user = testUtil.createUser("cspath1@ycp.edu")
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
               userRepo = userRepo
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
                userRepo = userRepo
        ).execute()
        //info should be null because id 311 does not exist
        assertNull(info)
        assertNotNull(error)

    }
}