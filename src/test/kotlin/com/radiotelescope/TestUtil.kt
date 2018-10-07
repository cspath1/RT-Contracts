package com.radiotelescope

import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
internal class TestUtil {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    fun createUser(email: String): User {
        val user = User(
                firstName = "First Name",
                lastName = "Last Name",
                email = email,
                password = "Password"
        )

        user.active = true
        user.status = User.Status.Active
        return userRepo.save(user)
    }

    fun createUserWithEncodedPassword(email: String, password: String): User {
        val user = User(
                firstName = "First Name",
                lastName = "Last Name",
                email = email,
                password = password
        )

        user.active = true
        user.status = User.Status.Active
        return userRepo.save(user)
    }

    fun createUserRolesForUser(userId: Long, role: UserRole.Role, isApproved: Boolean): List<UserRole> {
        // Creates a User UserRole by default
        val userRole = UserRole(
                userId = userId,
                role = UserRole.Role.USER
        )

        userRole.approved = true
        userRoleRepo.save(userRole)

        val otherRole = UserRole(
                userId = userId,
                role = role
        )

        otherRole.approved = isApproved
        userRoleRepo.save(otherRole)

        return listOf(userRole, otherRole)
    }
}