package com.radiotelescope

import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.telescope.Telescope
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
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
    private lateinit var allottedTimeCapRepo: IAllottedTimeCapRepository

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

    @Autowired
    private lateinit var updateEmailTokenRepo: IUpdateEmailTokenRepository

    @Autowired
    private lateinit var telescopeRepo: ITelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

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
                password = User.rtPasswordEncoder.encode(password)
        )

        user.active = true
        user.status = User.Status.ACTIVE
        return userRepo.save(user)
    }

    fun createUserRolesForUser(
            user: User,
            role: UserRole.Role,
            isApproved: Boolean
    ): List<UserRole> {
        // Creates a User UserRole by default
        val userRole = UserRole(
                user = user,
                role = UserRole.Role.USER
        )

        userRole.approved = true
        userRoleRepo.save(userRole)

        val otherRole = UserRole(
                user = user,
                role = role
        )

        otherRole.approved = isApproved
        userRoleRepo.save(otherRole)

        return listOf(userRole, otherRole)
    }

    fun createUserRoleForUser(
            user: User,
            role: UserRole.Role,
            isApproved: Boolean
    ): UserRole {
        val userRole = UserRole(
                user = user,
                role = role
        )

        userRole.approved = isApproved

        return userRoleRepo.save(userRole)
    }

    fun createAllottedTimeCapForUser(
            user: User,
            allottedTime: Long?
    ): AllottedTimeCap{
        val userTimeCap = AllottedTimeCap(
                user = user,
                allottedTime = allottedTime
        )

        return allottedTimeCapRepo.save(userTimeCap)
    }

    fun createAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean
    ): Appointment {
        val coordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesSecondsToDegrees(
                        hours = 12,
                        minutes = 12,
                        seconds = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12,
                seconds = 12
        )

        coordinateRepo.save(coordinate)

        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic
        )

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.coordinate = coordinate

        return appointmentRepo.save(theAppointment)
    }

    fun createLog(
            user: User?,
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

        theLog.user = user
        theLog.isSuccess = isSuccess

        return logRepo.save(theLog)
    }

    fun createErrorLog(
            user: User?,
            affectedRecordId: Long?,
            affectedTable: Log.AffectedTable,
            action: String,
            timestamp: Date,
            isSuccess: Boolean,
            errors: Map<String, Collection<String>>
    ): Log {
        val theLog = createLog(
                user = user,
                affectedRecordId = affectedRecordId,
                affectedTable = affectedTable,
                action = action,
                timestamp = timestamp,
                isSuccess = isSuccess
        )

        errors.keys.forEach {
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

    fun createUpdateEmailToken(
            user: User,
            token: String,
            email: String
    ) : UpdateEmailToken {
        val theUpdateEmailToken = UpdateEmailToken(
                token = token,
                expirationDate = Date(System.currentTimeMillis() + (1 * 24 * 60 * 60 * 1000)),
                email = email
        )

        theUpdateEmailToken.user = user

        return updateEmailTokenRepo.save(theUpdateEmailToken)
    }

    fun createTelescope(): Telescope {
        val telescope = Telescope()

        return telescopeRepo.save(telescope)
    }

    fun banUser(
            user: User
    ): User{
        user.active = false
        user.status = User.Status.BANNED
        return userRepo.save(user)
    }
}