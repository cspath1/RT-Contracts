package com.radiotelescope.controller.model.spectracyberConfig

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.spectracyberConfig.ErrorTag
import com.radiotelescope.controller.model.BaseForm
import com.radiotelescope.contracts.spectracyberConfig.Update

/**
 * Update form that takes nullable versions of the [Update.Request] object.
 * It is in charge of making sure these values are not null before adapting
 * it into a [Update.Request] object
 *
 * @param id the spectracyber config id
 * @param mode the spectracyber mode
 * @param integrationTime the integration time
 * @param offsetVoltage the offset voltage
 * @param IFGain the infrared gain
 * @param DCGain the direct current gain
 * @param bandwidth the bandwidth
 */
data class UpdateForm(
        val id: Long?,
        val mode: String?,
        val integrationTime: Double?,
        val offsetVoltage: Double?,
        val IFGain: Double?,
        val DCGain: Int?,
        val bandwidth: Int?
) : BaseForm<Update.Request> {
    /**
     * Override of the [BaseForm.toRequest] method that adapts
     * the form into [Update.Request] object
     */
    override fun toRequest(): Update.Request {
        return Update.Request(
                id = id!!,
                mode = mode!!,
                integrationTime = integrationTime!!,
                offsetVoltage = offsetVoltage!!,
                IFGain = IFGain!!,
                DCGain = DCGain!!,
                bandwidth = bandwidth!!
        )
    }

    /**
     * Makes sure all of the required fields are not null
     *
     * @return a [HashMultimap] of errors or null
     */
    fun validateRequest(): Multimap<ErrorTag, String>? {
        val errors = HashMultimap.create<ErrorTag, String>()

        if (id == null || id <= 0)
            errors.put(ErrorTag.ID, "ID may not be blank")
        if (mode == null)
            errors.put(ErrorTag.MODE, "Mode may not be blank")
        if (integrationTime == null)
            errors.put(ErrorTag.INTEGRATION_TIME, "Integration Time may not be blank")
        if (offsetVoltage == null)
            errors.put(ErrorTag.OFFSET_VOLTAGE, "Offset Voltage may not be blank")
        if (IFGain == null)
            errors.put(ErrorTag.IF_GAIN, "Infrared Gain may not be blank")
        if (DCGain == null)
            errors.put(ErrorTag.DC_GAIN, "DC Gain may not be blank")
        if (bandwidth == null)
            errors.put(ErrorTag.BANDWIDTH, "Bandwidth may not be blank")

        return if (errors.isEmpty) null else errors
    }
}