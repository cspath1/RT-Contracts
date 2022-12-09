package com.radiotelescope.controller.spring

import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.feedback.IFeedbackRepository
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.rfdata.IRFDataRepository
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.service.s3.IAwsS3DeleteService
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

/**
 * Spring Beans class used to autowire all Spring Repositories
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param logRepo the [ILogRepository] interface
 * @param errorRepo the [IErrorRepository] interface
 * @param radioTelescopeRepo the [IRadioTelescopeRepository] interface
 * @param appointmentRepo the [IAppointmentRepository] interface
 * @param rfDataRepo the [IRFDataRepository] interface
 * @param resetPasswordTokenRepo the [IResetPasswordTokenRepository] interface
 * @param accountActivateTokenRepo the [IAccountActivateTokenRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 * @param updateEmailTokenRepo the [IUpdateEmailTokenRepository] interface
 * @param coordinateRepo the [ICoordinateRepository] interface
 * @param celestialBodyRepo the [ICelestialBodyRepository] interface
 * @param viewerRepo the [IViewerRepository] interface
 * @param feedbackRepo the [IFeedbackRepository] interface
 * @param orientationRepo the [IOrientationRepository] interface
 * @param loginAttemptRepo the [ILoginAttemptRepository] interface
 * @param videoFileRepo the [IVideoFileRepository] interface
 * @param weatherDataRepo the [IWeatherDataRepository] interface
 * @param spectracyberConfigRepo the [ISpectracyberConfigRepository] interface
 * @param sensorStatusRepo the [ISensorStatusRepository] interface
 * @param thresholdsRepo the [IThresholdsRepository] interface
 * @param sensorOverridesRepo the [ISensorOverridesRepository] interface
 *
 */
@Component
@Configuration
class RepositoryBeans(
        val userRepo: IUserRepository,
        val userRoleRepo: IUserRoleRepository,
        val logRepo: ILogRepository,
        val errorRepo: IErrorRepository,
        val radioTelescopeRepo: IRadioTelescopeRepository,
        val appointmentRepo: IAppointmentRepository,
        val rfDataRepo: IRFDataRepository,
        val resetPasswordTokenRepo: IResetPasswordTokenRepository,
        val accountActivateTokenRepo: IAccountActivateTokenRepository,
        val updateEmailTokenRepo: IUpdateEmailTokenRepository,
        val allottedTimeCapRepo: IAllottedTimeCapRepository,
        val viewerRepo: IViewerRepository,
        val coordinateRepo: ICoordinateRepository,
        val celestialBodyRepo: ICelestialBodyRepository,
        val feedbackRepo: IFeedbackRepository,
        val orientationRepo: IOrientationRepository,
        val loginAttemptRepo: ILoginAttemptRepository,
        val videoFileRepo: IVideoFileRepository,
        val weatherDataRepo: IWeatherDataRepository,
        val spectracyberConfigRepo: ISpectracyberConfigRepository,
        val sensorStatusRepo: ISensorStatusRepository,
        val thresholdsRepo: IThresholdsRepository,
        val sensorOverridesRepo: ISensorOverridesRepository,
        val frontpagePictureRepo: IFrontpagePictureRepository
)