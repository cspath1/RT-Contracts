package com.radiotelescope.controller.spring

import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.telescope.ITelescopeRepository
import com.radiotelescope.repository.user.IUserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/**
 * Spring Beans class used to autowire all Spring Repositories
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param logRepo the [ILogRepository] interface
 * @param errorRepo the [IErrorRepository] interface
 * @param telescopeRepo the [ITelescopeRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param rfDataRepo the [IRFDataRepository] interface
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository] interface
 * @param accountActivateTokenRepo the [IAccountActivateTokenRepository] interface
 *
 */
@Component
@Configuration
class RepositoryBeans(
        val userRepo: IUserRepository,
        val userRoleRepo: IUserRoleRepository,
        val logRepo: ILogRepository,
        val errorRepo: IErrorRepository,
        val telescopeRepo: ITelescopeRepository,
        val appointmentRepo: IAppointmentRepository,
        val rfDataRepo: IRFDataRepository,
        val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        val accountActivateTokenRepo: IAccountActivateTokenRepository
)