package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.accountActivateToken.BaseAccountActivateTokenFactory
import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.contracts.appointment.factory.CoordinateAppointmentFactory
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.appointment.factory.CelestialBodyAppointmentFactory
import com.radiotelescope.contracts.appointment.factory.RasterScanAppointmentFactory
import com.radiotelescope.contracts.celestialBody.BaseCelestialBodyFactory
import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.contracts.feedback.BaseFeedbackFactory
import com.radiotelescope.contracts.feedback.UserFeedbackWrapper
import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.log.BaseLogFactory
import com.radiotelescope.contracts.resetPasswordToken.BaseResetPasswordTokenFactory
import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.contracts.rfdata.BaseRFDataFactory
import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.contracts.role.BaseUserRoleFactory
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.updateEmailToken.BaseUpdateEmailTokenFactory
import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.contracts.user.BaseUserFactory
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.viewer.BaseViewerFactory
import com.radiotelescope.contracts.viewer.UserViewerWrapper
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
     * Returns a [UserUserWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean
    override fun getUserWrapper(): UserUserWrapper {
        return UserUserWrapper(
                context = userContext,
                factory = BaseUserFactory(
                        userRepo = repositories.userRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        accountActivateTokenRepo = repositories.accountActivateTokenRepo,
                        allottedTimeCapRepo = repositories.allottedTimeCapRepo
                ),
                userRepo = repositories.userRepo
        )
    }

    /**
     * Returns a [UserUserRoleWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean
    override fun getUserRoleWrapper(): UserUserRoleWrapper {
        return UserUserRoleWrapper(
                context = userContext,
                factory = BaseUserRoleFactory(
                        userRepo = repositories.userRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        allottedTimeCapRepo = repositories.allottedTimeCapRepo
                ),
                userRepo = repositories.userRepo,
                userRoleRepo = repositories.userRoleRepo
        )
    }

    /**
     * Returns a [UserAppointmentWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean(value = ["coordinateAppointmentWrapper"])
    override fun getCoordinateAppointmentWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = userContext,
                factory = CoordinateAppointmentFactory(
                        userRepo = repositories.userRepo,
                        appointmentRepo = repositories.appointmentRepo,
                        telescopeRepo = repositories.telescopeRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        coordinateRepo = repositories.coordinateRepo,
                        orientationRepo = repositories.orientationRepo,
                        allottedTimeCapRepo = repositories.allottedTimeCapRepo
                ),
                appointmentRepo = repositories.appointmentRepo,
                viewerRepo = repositories.viewerRepo
        )
    }

    @Bean(value = ["celestialBodyAppointmentWrapper"])
    override fun getCelestialBodyAppointmentWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = userContext,
                factory = CelestialBodyAppointmentFactory(
                        userRepo = repositories.userRepo,
                        appointmentRepo = repositories.appointmentRepo,
                        telescopeRepo = repositories.telescopeRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        celestialBodyRepo = repositories.celestialBodyRepo,
                        coordinateRepo = repositories.coordinateRepo,
                        orientationRepo = repositories.orientationRepo,
                        allottedTimeCapRepo = repositories.allottedTimeCapRepo
                ),
                appointmentRepo = repositories.appointmentRepo,
                viewerRepo = repositories.viewerRepo
        )
    }

    @Bean(value = ["rasterScanAppointmentWrapper"])
    override fun getRasterScanAppointmentWrapper(): UserAppointmentWrapper {
        return UserAppointmentWrapper(
                context = userContext,
                factory = RasterScanAppointmentFactory(
                        userRepo = repositories.userRepo,
                        appointmentRepo = repositories.appointmentRepo,
                        telescopeRepo = repositories.telescopeRepo,
                        userRoleRepo = repositories.userRoleRepo,
                        coordinateRepo = repositories.coordinateRepo,
                        allottedTimeCapRepo = repositories.allottedTimeCapRepo,
                        orientationRepo = repositories.orientationRepo
                ),
                appointmentRepo = repositories.appointmentRepo,
                viewerRepo = repositories.viewerRepo
        )
    }

    /**
     * Returns a [UserRFDataWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean
    override fun getRFDataWrapper(): UserRFDataWrapper {
        return UserRFDataWrapper(
                context = userContext,
                factory = BaseRFDataFactory(
                        appointmentRepo = repositories.appointmentRepo,
                        rfDataRepo = repositories.rfDataRepo
                ),
                appointmentRepo = repositories.appointmentRepo,
                viewerRepo = repositories.viewerRepo
        )
    }

    /**
     * Returns a [AdminLogWrapper] object, allowing it to be autowired
     * in the controllers
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
     * Returns a [UserResetPasswordTokenWrapper] object, allowing it to be autowired
     * in the controllers
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
     * Returns a [UserAccountActivateTokenWrapper] object, allowing it to be autowired
     * in the controllers
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

    /**
     * Returns a [UserUpdateEmailTokenWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean
    override fun getUpdateEmailTokenWrapper(): UserUpdateEmailTokenWrapper {
        return UserUpdateEmailTokenWrapper(
                factory = BaseUpdateEmailTokenFactory(
                        updateEmailTokenRepo = repositories.updateEmailTokenRepo,
                        userRepo = repositories.userRepo
                ),
                context = userContext,
                userRepo = repositories.userRepo
        )
    }

    /**
     * Returns a [UserViewerWrapper] object, allowing it to be autowired
     * in the controllers
     */
    @Bean
    override fun getViewerWrapper(): UserViewerWrapper {
        return UserViewerWrapper(
                factory = BaseViewerFactory(
                        viewerRepo = repositories.viewerRepo,
                        userRepo = repositories.userRepo,
                        appointmentRepo = repositories.appointmentRepo
                ),
                context = userContext,
                appointmentRepo = repositories.appointmentRepo
        )
    }

    /**
     * Returns a [UserCelestialBodyWrapper] object, allowing it to be autowired
     * in the controller
     */
    @Bean
    override fun getCelestialBodyWrapper(): UserCelestialBodyWrapper {
        return UserCelestialBodyWrapper(
                context = userContext,
                factory = BaseCelestialBodyFactory(
                        celestialBodyRepo = repositories.celestialBodyRepo,
                        coordinateRepo = repositories.coordinateRepo
                )
        )
    }

    /**
     * Returns a [UserFeedbackWrapper] object, allowing it to be autowired
     * in controllers
     */
    @Bean
    override fun getFeedbackWrapper(): UserFeedbackWrapper {
        return UserFeedbackWrapper(
                factory = BaseFeedbackFactory(
                        feedbackRepo = repositories.feedbackRepo
                )
        )
    }
}