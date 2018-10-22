package com.radiotelescope.contracts.user

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.jayway.jsonpath.internal.path.PathCompiler.fail
import com.radiotelescope.TestUtil
import com.radiotelescope.repository.role.IUserRoleRepository
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
internal class BanTest {

    @TestConfiguration
    class UtilTestContextConfiguration {
        @Bean
        fun utilService(): TestUtil {
            return TestUtil()
        }
    }

    @Autowired
    private lateinit var testUtil: TestUtil

    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    private lateinit var factory: UserFactory

    //persist a User
    var globalB: Long? = 0
    var globalE: Multimap<ErrorTag, String>? = HashMultimap.create<ErrorTag, String>()
    var globalId: Long = 0

    //status initially set to Active
    var globalStatus: User.Status = User.Status.Active

    @Before
    fun setUp() {

        var u: User = testUtil.createUser("jamoros@ycp.edu")

        var b = Ban(userRepo, u.id).execute().success
        var e = Ban(userRepo, u.id).execute().error

        globalB = b
        globalId = u.id
        globalE = e
        globalStatus =  u.status
    }

    @Test
    fun BanTest1() {

        //if success is null, error case
        if (globalB == null) {
            fail("error case")
        }
//else success case
        else {//pass
            assert(globalB == globalId)

        }
    }
}