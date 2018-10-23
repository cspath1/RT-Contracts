package com.radiotelescope

import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
internal class TestUtil {
    @Autowired
    private lateinit var userRepo: IUserRepository

    @Autowired
    private lateinit var userRoleRepo: IUserRoleRepository

    @Autowired
    private lateinit var appointmentRepo: IAppointmentRepository

    @Autowired
    private lateinit var resetPasswordTokenRepo: IResetPasswordTokenRepository

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

    fun createUserWithEncodedPassword(
            email: String,
            password: String
    ): User {
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

    fun createUserRolesForUser(
            userId: Long,
            role: UserRole.Role,
            isApproved: Boolean
    ): List<UserRole> {
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

    fun createAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean
    ): Appointment {
        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic
        )

        theAppointment.status = status
        theAppointment.user = user

        return appointmentRepo.save(theAppointment)
    }

    fun createResetPasswordToken(
            user: User
    ) : ResetPasswordToken {
        val theResetPasswordToken = ResetPasswordToken(
            token = "someToken",
            expiryDate = Date(System.currentTimeMillis() + 10000L)
        )

        theResetPasswordToken.user = user

        return resetPasswordTokenRepo.save(theResetPasswordToken)
    }
}