package com.radiotelescope

import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
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

    @Autowired
    private lateinit var errorRepo: IErrorRepository

    @Autowired
    private lateinit var logRepo: ILogRepository

    @Autowired
    private lateinit var accountActivateTokenRepo: IAccountActivateTokenRepository

    fun createUser(email: String): User {
        val user = User(
                firstName = "First Name",
                lastName = "Last Name",
                email = email,
                password = "Password"
        )

        user.active = true
        user.status = User.Status.ACTIVE
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
        user.status = User.Status.ACTIVE
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

    fun createLog(
            userId: Long?,
            affectedRecordId: Long?,
            affectedTable: Log.AffectedTable,
            action: String,
            timestamp: Date,
            isSuccess: Boolean
    ): Log {
        val theLog = Log(
                affectedTable = affectedTable,
                action = action,
                timestamp = timestamp,
                affectedRecordId = null
        )

        theLog.userId = userId
        theLog.isSuccess = isSuccess

        return logRepo.save(theLog)
    }

    fun createErrorLog(
            userId: Long?,
            affectedRecordId: Long?,
            affectedTable: Log.AffectedTable,
            action: String,
            timestamp: Date,
            isSuccess: Boolean,
            errors: Map<String, Collection<String>>
    ): Log {
        val theLog = createLog(
                userId = userId,
                affectedRecordId = affectedRecordId,
                affectedTable = affectedTable,
                action = action,
                timestamp = timestamp,
                isSuccess = isSuccess
        )

        errors.keys.forEach { it ->
            val errorList = errors[it]
            if (errorList != null && errorList.isNotEmpty()) {
                errorList.forEach { error ->
                    val err = Error(
                            log = theLog,
                            field = it,
                            message = error
                    )

                    errorRepo.save(err)
                    theLog.errors.add(err)
                }
            }
        }

        return logRepo.save(theLog)
    }

    fun setInactiveStatus(
            user: User
    ): User{
        user.active = false
        user.status = User.Status.INACTIVE
        return userRepo.save(user)
    }

    fun createResetPasswordToken(
            user: User
    ) : ResetPasswordToken {
        val theResetPasswordToken = ResetPasswordToken(
            token = "someToken",
            expirationDate = Date(System.currentTimeMillis() + 10000L)
        )

        theResetPasswordToken.user = user

        return resetPasswordTokenRepo.save(theResetPasswordToken)
    }

    fun createAccountActivateToken(
            user: User,
            token: String
    ) : AccountActivateToken {
        val theAccountActivateToken = AccountActivateToken(
                token = token,
                expirationDate = Date(System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000))
        )

        theAccountActivateToken.user = user

        return accountActivateTokenRepo.save(theAccountActivateToken)
    }
}