package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.contracts.appointment.wrapper.UserAutoAppointmentWrapper
import com.radiotelescope.contracts.appointment.create.CoordinateAppointmentCreate
import com.radiotelescope.contracts.appointment.create.CelestialBodyAppointmentCreate
import com.radiotelescope.contracts.appointment.create.RasterScanAppointmentCreate
import com.radiotelescope.contracts.appointment.wrapper.UserManualAppointmentWrapper
import com.radiotelescope.contracts.celestialBody.UserCelestialBodyWrapper
import com.radiotelescope.contracts.feedback.UserFeedbackWrapper
import com.radiotelescope.contracts.log.AdminLogWrapper
import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.updateEmailToken.UserUpdateEmailTokenWrapper
import com.radiotelescope.contracts.user.UserUserWrapper
import com.radiotelescope.contracts.viewer.UserViewerWrapper

/**
 * Interface to get instantiations of all User Wrappers
 */
interface FactoryProvider {
    /**
     * Abstract method to return the [UserUserWrapper] class
     */
    fun getUserWrapper(): UserUserWrapper

    /**
     * Abstract method to return the [UserUserRoleWrapper] class
     */
    fun getUserRoleWrapper(): UserUserRoleWrapper

    /**
     * Abstract method to return the [UserAutoAppointmentWrapper] class
     * for Point appointments
     */
    fun getCoordinateAppointmentWrapper(): UserAutoAppointmentWrapper

    /**
     * Abstract method to return the [UserAutoAppointmentWrapper] class
     * for Celestial Body appointments
     */
    fun getCelestialBodyAppointmentWrapper(): UserAutoAppointmentWrapper

    /**
     * Abstract method to return the [UserAutoAppointmentWrapper] class
     * for Raster Scan appointments
     */
    fun getRasterScanAppointmentWrapper(): UserAutoAppointmentWrapper

    /**
     * Abstract method to return the [UserManualAppointmentWrapper] class
     * for Free Control appointments
     */
    fun getFreeControlAppointmentWrapper(): UserManualAppointmentWrapper

    /**
     * Abstract method to return the [UserRFDataWrapper] class
     */
    fun getRFDataWrapper(): UserRFDataWrapper

    /**
     * Abstract method to return the [AdminLogWrapper] class
     */
    fun getLogWrapper(): AdminLogWrapper

    /**
     * Abstract method to return the [UserResetPasswordTokenWrapper] class
     */
    fun getResetPasswordTokenWrapper(): UserResetPasswordTokenWrapper

    /**
     * Abstract method to return the [UserAccountActivateTokenWrapper] class
     */
    fun getAccountActivateTokenWrapper(): UserAccountActivateTokenWrapper

    /**
     * Abstract method to return the [UserUpdateEmailTokenWrapper] class
     */
    fun getUpdateEmailTokenWrapper(): UserUpdateEmailTokenWrapper

    /**
     * Abstract method to return the [UserViewerWrapper] class
     */
    fun getViewerWrapper(): UserViewerWrapper

    /**
     * Abstract method to return the [UserCelestialBodyWrapper] class
     */
    fun getCelestialBodyWrapper(): UserCelestialBodyWrapper

    /**
     * Abstract method to return the [UserFeedbackWrapper] class
     */
    fun getFeedbackWrapper(): UserFeedbackWrapper
}