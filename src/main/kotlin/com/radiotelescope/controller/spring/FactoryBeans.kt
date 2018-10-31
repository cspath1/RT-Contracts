package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.accountActivateToken.BaseAccountActivateTokenFactory
import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.contracts.appointment.BaseAppointmentFactory
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.log.BaseLogFactory
import com.radiotelescope.contracts.resetPasswordToken.BaseResetPasswordTokenFactory
import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.contracts.rfdata.BaseRFDataFactory
import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.contracts.role.BaseUserRoleFactory
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.user.BaseUserFactory
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.security.UserContextImpl
import com.radiotelescope.security.service.RetrieveAuthUserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Concrete implementation of the [FactoryProvider] interface. It is in charge
 * of making sure the UserWrappers are able to be autowired by Spring when the
 * server is started
 *
 * @param repositories the [RepositoryBeans] Spring component
 * @param retrieveAuthUserService the [RetrieveAuthUserService] service
 */
@Configuration
class FactoryBeans(
        private var repositories: RepositoryBeans,
        retrieveAuthUserService: RetrieveAuthUserService
) : FactoryProvider {
    private val userContext = UserContextImpl(
            userRepo = repositories.userRepo,
            userRoleRepo = repositories.userRoleRepo,
            retrieveAuthUserService = retrieveAuthUserService
    )

    /**
     * Returns a [UserUserWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getUserWrapper(): UserUserWrapper {
        return UserUserWrapper(
                context = userContext,
                factory = BaseUserFactory(
                        userRepo = repositories.userRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        accountActivateTokenRepo = repositories.accountActivateTokenRepo
                ),
                userRepo = repositories.userRepo
        )
    }

    /**
     * Returns a [UserUserRoleWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getUserRoleWrapper(): UserUserRoleWrapper {
        return UserUserRoleWrapper(
                context = userContext,
                factory = BaseUserRoleFactory(
                        userRepo = repositories.userRepo,
                        userRoleRepo = repositories.userRoleRepo
                ),
                userRepo = repositories.userRepo,
                userRoleRepo = repositories.userRoleRepo
        )
    }

    /**
     * Returns a [UserAppointmentWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getAppointmentWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = userContext,
                factory = BaseAppointmentFactory(
                        userRepo = repositories.userRepo,
                        appointmentRepo = repositories.appointmentRepo,
                        telescopeRepo = repositories.telescopeRepo,
                        userRoleRepo = repositories.userRoleRepo
                ),
                appointmentRepo = repositories.appointmentRepo
        )
    }

    /**
     * Returns a [UserRFDataWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getRFDataWrapper(): UserRFDataWrapper {
        return UserRFDataWrapper(
                context = userContext,
                factory = BaseRFDataFactory(
                        appointmentRepo = repositories.appointmentRepo,
                        rfDataRepo = repositories.rfDataRepo
                ),
                appointmentRepo = repositories.appointmentRepo
        )
    }

    /**
     * Returns a [AdminLogWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getLogWrapper(): AdminLogWrapper {
        return AdminLogWrapper(
                context = userContext,
                factory = BaseLogFactory(
                        logRepo = repositories.logRepo,
                        userRepo = repositories.userRepo
                )
        )
    }

    /**
     * Returns a [UserResetPasswordTokenWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getResetPasswordTokenWrapper(): UserResetPasswordTokenWrapper {
        return UserResetPasswordTokenWrapper(
                resetPasswordTokenFactory = BaseResetPasswordTokenFactory(
                        resetPasswordTokenRepo = repositories.resetPasswordTokenRepo,
                        userRepo = repositories.userRepo
                )
        )
    }

    /**
     * Returns a [UserAccountActivateTokenWrapper] object. This allows these objects
     * to be autowired in the controllers
     */
    @Bean
    override fun getAccountActivateTokenWrapper(): UserAccountActivateTokenWrapper {
        return UserAccountActivateTokenWrapper(
                factory = BaseAccountActivateTokenFactory(
                        accountActivateTokenRepo = repositories.accountActivateTokenRepo,
                        userRepo = repositories.userRepo
                )
        )
    }
}