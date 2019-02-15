package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.accountActivateToken.UserAccountActivateTokenWrapper
import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
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
     * Abstract method to return the [UserAppointmentWrapper] class
     */
    fun getAppointmentWrapper(): UserAppointmentWrapper

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
}