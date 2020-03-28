package com.radiotelescope

import com.radiotelescope.repository.accountActivateToken.AccountActivateToken
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.AllottedTimeCap
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.appointment.Appointment
import com.radiotelescope.repository.appointment.IAppointmentRepository
import com.radiotelescope.repository.celestialBody.CelestialBody
import com.radiotelescope.repository.celestialBody.ICelestialBodyRepository
import com.radiotelescope.repository.error.Error
import com.radiotelescope.repository.error.IErrorRepository
import com.radiotelescope.repository.log.ILogRepository
import com.radiotelescope.repository.log.Log
import com.radiotelescope.repository.coordinate.ICoordinateRepository
import com.radiotelescope.repository.coordinate.Coordinate
import com.radiotelescope.repository.frontpagePicture.IFrontpagePictureRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.loginAttempt.LoginAttempt
import com.radiotelescope.repository.orientation.IOrientationRepository
import com.radiotelescope.repository.orientation.Orientation
import com.radiotelescope.repository.resetPasswordToken.IResetPasswordTokenRepository
import com.radiotelescope.repository.resetPasswordToken.ResetPasswordToken
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.role.UserRole
import com.radiotelescope.repository.sensorOverrides.ISensorOverridesRepository
import com.radiotelescope.repository.sensorOverrides.SensorOverrides
import com.radiotelescope.repository.sensorStatus.ISensorStatusRepository
import com.radiotelescope.repository.sensorStatus.SensorStatus
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig
import com.radiotelescope.repository.telescope.IRadioTelescopeRepository
import com.radiotelescope.repository.thresholds.IThresholdsRepository
import com.radiotelescope.repository.thresholds.Thresholds
import com.radiotelescope.repository.updateEmailToken.IUpdateEmailTokenRepository
import com.radiotelescope.repository.updateEmailToken.UpdateEmailToken
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.repository.videoFile.IVideoFileRepository
import com.radiotelescope.repository.videoFile.VideoFile
import com.radiotelescope.repository.weatherData.IWeatherDataRepository
import com.radiotelescope.repository.weatherData.WeatherData
import com.radiotelescope.repository.viewer.IViewerRepository
import com.radiotelescope.repository.viewer.Viewer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
    private lateinit var radioTelescopeRepo: IRadioTelescopeRepository

    @Autowired
    private lateinit var coordinateRepo: ICoordinateRepository

    @Autowired
    private lateinit var viewerRepo: IViewerRepository

    @Autowired
    private lateinit var celestialBodyRepo: ICelestialBodyRepository

    @Autowired
    private lateinit var orientationRepo: IOrientationRepository

    @Autowired
    private lateinit var loginAttemptRepo: ILoginAttemptRepository

    @Autowired
    private lateinit var videoFileRepo: IVideoFileRepository

    @Autowired
    private lateinit var sensorStatusRepo: ISensorStatusRepository

    @Autowired
    private lateinit var weatherDataRepo: IWeatherDataRepository

    @Autowired
    private lateinit var thresholdsRepo: IThresholdsRepository

    @Autowired
    private lateinit var spectracyberConfigRepo: ISpectracyberConfigRepository

    @Autowired
    private lateinit var sensorOverridesRepo: ISensorOverridesRepository

    @Autowired
    private lateinit var frontpagePictureRepo: IFrontpagePictureRepository

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
            isPublic: Boolean,
            priority: Appointment.Priority,
            type: Appointment.Type
    ): Appointment {
        when (type) {
            Appointment.Type.POINT -> return createPointAppointment(
                    user = user,
                    telescopeId = telescopeId,
                    status = status,
                    startTime = startTime,
                    endTime = endTime,
                    isPublic = isPublic,
                    priority = priority
            )
            Appointment.Type.CELESTIAL_BODY -> return createCelestialBodyAppointment(
                    user = user,
                    telescopeId = telescopeId,
                    status = status,
                    startTime = startTime,
                    endTime = endTime,
                    isPublic = isPublic,
                    priority = priority
            )
            Appointment.Type.RASTER_SCAN -> return createRasterScanAppointment(
                    user = user,
                    telescopeId = telescopeId,
                    status = status,
                    startTime = startTime,
                    endTime = endTime,
                    isPublic = isPublic,
                    priority = priority
            )
            Appointment.Type.DRIFT_SCAN -> return createDriftScanAppointment(
                    user = user,
                    telescopeId = telescopeId,
                    status = status,
                    startTime = startTime,
                    endTime = endTime,
                    isPublic = isPublic,
                    priority = priority
            )
            Appointment.Type.FREE_CONTROL -> return createFreeControlAppointment(
                    user = user,
                    telescopeId = telescopeId,
                    status = status,
                    startTime = startTime,
                    endTime = endTime,
                    isPublic = isPublic,
                    priority = priority
            )
        }
    }

    private fun createFreeControlAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean,
            priority: Appointment.Priority
    ): Appointment {
        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic,
                priority = priority,
                type = Appointment.Type.FREE_CONTROL
        )

        val startingCoordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12
        )

        coordinateRepo.save(startingCoordinate)

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.coordinateList = mutableListOf(startingCoordinate)
        theAppointment.spectracyberConfig = createDefaultSpectracyberConfig()
        appointmentRepo.save(theAppointment)

        startingCoordinate.appointment = theAppointment
        coordinateRepo.save(startingCoordinate)

        return appointmentRepo.save(theAppointment)
    }

    private fun createDriftScanAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean,
            priority: Appointment.Priority
    ): Appointment {
        val orientation = Orientation(
                azimuth = 66.6,
                elevation = 45.0
        )

        orientationRepo.save(orientation)

        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic,
                priority = priority,
                type = Appointment.Type.DRIFT_SCAN
        )

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.orientation = orientation
        theAppointment.spectracyberConfig = createDefaultSpectracyberConfig()

        return appointmentRepo.save(theAppointment)
    }

    private fun createRasterScanAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean,
            priority: Appointment.Priority
    ): Appointment {
        val coordinateOne = Coordinate(
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12
        )

        coordinateRepo.save(coordinateOne)

        val coordinateTwo = Coordinate(
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12
        )

        coordinateRepo.save(coordinateTwo)

        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic,
                priority = priority,
                type = Appointment.Type.RASTER_SCAN
        )

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.coordinateList = arrayListOf(coordinateOne, coordinateTwo)
        theAppointment.spectracyberConfig = createDefaultSpectracyberConfig()
        appointmentRepo.save(theAppointment)

        coordinateOne.appointment = theAppointment
        coordinateTwo.appointment = theAppointment
        coordinateRepo.save(coordinateOne)
        coordinateRepo.save(coordinateTwo)

        return appointmentRepo.save(theAppointment)
    }

    private fun createCelestialBodyAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean,
            priority: Appointment.Priority
    ): Appointment {
        val coordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12
        )

        coordinateRepo.save(coordinate)

        val celestialBody = createCelestialBody(
                name = "A Celestial Body",
                coordinate = coordinate
        )

        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic,
                priority = priority,
                type = Appointment.Type.CELESTIAL_BODY
        )

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.celestialBody = celestialBody
        theAppointment.spectracyberConfig = createDefaultSpectracyberConfig()

        return appointmentRepo.save(theAppointment)
    }

    private fun createPointAppointment(
            user: User,
            telescopeId: Long,
            status: Appointment.Status,
            startTime: Date,
            endTime: Date,
            isPublic: Boolean,
            priority: Appointment.Priority
    ): Appointment {
        val coordinate = Coordinate(
                rightAscension = Coordinate.hoursMinutesToDegrees(
                        hours = 12,
                        minutes = 12
                ),
                declination = 69.0,
                hours = 12,
                minutes = 12
        )

        coordinateRepo.save(coordinate)

        val theAppointment = Appointment(
                startTime = startTime,
                endTime = endTime,
                telescopeId = telescopeId,
                isPublic = isPublic,
                priority = priority,
                type = Appointment.Type.POINT
        )

        theAppointment.status = status
        theAppointment.user = user
        theAppointment.coordinateList = arrayListOf(coordinate)
        theAppointment.spectracyberConfig = createDefaultSpectracyberConfig()
        appointmentRepo.save(theAppointment)

        coordinate.appointment = theAppointment
        coordinateRepo.save(coordinate)

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
                action = action,
                timestamp = timestamp,
                affectedRecordId = null,
                status = HttpStatus.OK.value()
        )

        theLog.affectedTable = affectedTable
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

    fun banUser(user: User): User{
        user.active = false
        user.status = User.Status.BANNED

        return userRepo.save(user)
    }

    fun createCelestialBody(name: String, coordinate: Coordinate?): CelestialBody {
        val celestialBody = CelestialBody(name)
        celestialBody.coordinate = coordinate

        return celestialBodyRepo.save(celestialBody)
    }

    fun createViewer(
            user: User,
            appointment: Appointment
    ): Viewer {
        val viewer = Viewer()
        viewer.user = user
        viewer.appointment = appointment

        return viewerRepo.save(viewer)
    }

    fun createLoginAttempt(
            user: User
    ): LoginAttempt {
        val loginAttempt = LoginAttempt(Date(System.currentTimeMillis()))
        loginAttempt.user = user

        return loginAttemptRepo.save(loginAttempt)
    }

    fun createVideoFileRecord(
            thumbnailPath: String,
            videoPath: String,
            videoLength: String
    ): VideoFile {
        return videoFileRepo.save(VideoFile(thumbnailPath, videoPath, videoLength))
    }

    fun createWeatherDataRecord(
        windSpeed: Float,
        windDirectionDeg: Float,
        windDirectionStr: String,
        outsideTemperatureDegF: Float,
        insideTemperatureDegF: Float,
        rainRate: Float,
        rainTotal: Float,
        rainDay: Float,
        rainMonth: Float,
        barometricPressure: Float,
        dewPoint: Float,
        windChill: Float,
        humidity: Float,
        heatIndex: Float,
        timeCaptured: Int
    ): WeatherData {
        val weatherData = WeatherData(
                windSpeed = windSpeed,
                windDirectionDeg = windDirectionDeg,
                windDirectionStr = windDirectionStr,
                outsideTemperatureDegF = outsideTemperatureDegF,
                insideTemperatureDegF = insideTemperatureDegF,
                rainRate = rainRate,
                rainTotal = rainTotal,
                rainDay = rainDay,
                rainMonth = rainMonth,
                barometricPressure = barometricPressure,
                dewPoint = dewPoint,
                windChill = windChill,
                humidity = humidity,
                heatIndex = heatIndex,
                timeCaptured = timeCaptured
        )
        return weatherDataRepo.save(weatherData)
    }

    fun createSensorStatus(
            gate: Int,
            proximity: Int,
            azimuthMotor: Int,
            elevationMotor: Int,
            weatherStation: Int
    ): SensorStatus {
        return sensorStatusRepo.save(SensorStatus(gate, proximity, azimuthMotor, elevationMotor, weatherStation))
    }

    fun createDefaultSpectracyberConfig() : SpectracyberConfig {
        return spectracyberConfigRepo.save(SpectracyberConfig(SpectracyberConfig.Mode.SPECTRAL, 0.3, 0.0, 10.0, 1, 1200))
    }

    fun populateDefaultThresholds() {
        thresholdsRepo.save(Thresholds(Thresholds.Name.WIND, 30.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.AZ_MOTOR_TEMP, 80.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.ELEV_MOTOR_TEMP, 80.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.AZ_MOTOR_VIBRATION, 1.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.ELEV_MOTOR_VIBRATION, 1.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.AZ_MOTOR_CURRENT, 6.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.ELEV_MOTOR_CURRENT, 6.0))
        thresholdsRepo.save(Thresholds(Thresholds.Name.COUNTER_BALANCE_VIBRATION, 0.42))
    }

    fun populateDefaultSensorOverrides() {
        sensorOverridesRepo.save(SensorOverrides(SensorOverrides.Name.GATE, false))
        sensorOverridesRepo.save(SensorOverrides(SensorOverrides.Name.PROXIMITY, false))
        sensorOverridesRepo.save(SensorOverrides(SensorOverrides.Name.AZIMUTH_MOTOR, false))
        sensorOverridesRepo.save(SensorOverrides(SensorOverrides.Name.ELEVATION_MOTOR, false))
        sensorOverridesRepo.save(SensorOverrides(SensorOverrides.Name.WEATHER_STATION, false))
    }

}