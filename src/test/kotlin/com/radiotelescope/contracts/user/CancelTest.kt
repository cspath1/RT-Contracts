package com.radiotelescope.contracts.user

import com.radiotelescope.TestUtil
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner


@DataJpaTest
@RunWith(SpringRunner::class)
@ActiveProfiles(value = ["test"])
internal class CancelTest
{

    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil
        {
            return TestUtil()
        }
    }


    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var testUtil:TestUtil





    @Before
    fun setUp()
    {
      var u: User = testUtil.createUser("jamoros@ycp.edu")
              u.id
    }

    @Test
    fun CancelTest()
    {
        userRepo
    }
}


