package com.radiotelescope.controller.spring

import com.radiotelescope.contracts.appointment.UserAppointmentWrapper
import com.radiotelescope.contracts.resetPasswordToken.UserResetPasswordTokenWrapper
import com.radiotelescope.contracts.rfdata.UserRFDataWrapper
import com.radiotelescope.contracts.role.UserUserRoleWrapper
import com.radiotelescope.contracts.user.UserUserWrapper

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
     * Abstract method to return the [UserResetPasswordTokenWrapper] class
     */
    fun getResetPasswordTokenWrapper(): UserResetPasswordTokenWrapper
}