package com.radiotelescope.contracts.spectracyberConfig

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.radiotelescope.contracts.BaseUpdateRequest
import com.radiotelescope.contracts.Command
import com.radiotelescope.contracts.SimpleResult
import com.radiotelescope.repository.spectracyberConfig.ISpectracyberConfigRepository
import com.radiotelescope.repository.spectracyberConfig.SpectracyberConfig

/**
 * Override of the [Command] interface method used for SpectracyberConfig update
 *
 * @param request the [Request] object
 * @param spectracyberConfigRepo the [ISpectracyberConfigRepository]
 */
class Update(
        private val request: Update.Request,
        private val spectracyberConfigRepo: ISpectracyberConfigRepository
) : Command<Long, Multimap<ErrorTag, String>>{

    /**
     * Override of the [Command] execute method. Calls the [validateRequest] method
     * that will handle all constraint checking and validations.
     *
     * If validation passes, it will update and persist the [SpectracyberConfig] object
     * It will then return a [SimpleResult] object with the [SpectracyberConfig] id and a null errors field.
     *
     * If validation fields, it will return a [SimpleResult] with the errors and a
     * null success field
     */
    override fun execute(): SimpleResult<Long, Multimap<ErrorTag, String>> {
        val errors = validateRequest()

        if (!errors.isEmpty)
            return SimpleResult(null, errors)

        val spectracyberConfig = spectracyberConfigRepo.findById(request.id).get()
        val updatedSpectracyberConfig = spectracyberConfigRepo.save(request.updateEntity(spectracyberConfig))

        return SimpleResult(updatedSpectracyberConfig.id, null)
    }

    /**
     * Method responsible for constraint checking. Checks that [SpectracyberConfig] object exists,
     * then checks that each configuration option in the request is greater than or equal to 0
     */
    private fun validateRequest(): Multimap<ErrorTag, String> {
        val errors = HashMultimap.create<ErrorTag, String>()

        with(request) {
            if (spectracyberConfigRepo.existsById(id)) {
                if (mode != "SPECTRAL" && mode != "CONTINUUM" && mode != "UNKNOWN")
                    errors.put(ErrorTag.MODE, "Mode must be either SPECTRAL, CONTINUUM, or UNKNOWN")
                if (integrationTime < 0.0)
                    errors.put(ErrorTag.INTEGRATION_TIME, "Integration Time must be greater than or equal to 0 time/step")
                if (offsetVoltage < 0.0 || offsetVoltage > 4.095)
                    errors.put(ErrorTag.OFFSET_VOLTAGE, "Offset Voltage must be between 0 and 4.095 volts")
                if (IFGain < 10.0 || IFGain > 25.75)
                    errors.put(ErrorTag.IF_GAIN, "IF Gain must be between 10.0 and 25.75 decibels")
                if (DCGain != 1 && DCGain != 5 && DCGain != 10 && DCGain != 20 && DCGain != 50 && DCGain != 60)
                    errors.put(ErrorTag.DC_GAIN, "DC Gain must be 1, 5, 10, 20, 50, or 60")
                if (bandwidth < 0)
                    errors.put(ErrorTag.BANDWIDTH, "Bandwidth must be greater than or equal to 0")
            } else {
                errors.put(ErrorTag.ID, "No SpectracyberConfig was found with specified ID")
                return errors
            }
        }

        return errors
    }

    /**
     * Data class containing all fields necessary for user update. Implements the
     * [BaseUpdateRequest] interface and overrides the [BaseUpdateRequest.updateEntity]
     * method
     */
    data class Request(
            val id: Long,
            val mode: String,
            val integrationTime: Double,
            val offsetVoltage: Double,
            val IFGain: Double,
            val DCGain: Int,
            val bandwidth: Int
    ) : BaseUpdateRequest<SpectracyberConfig> {
        override fun updateEntity(entity: SpectracyberConfig): SpectracyberConfig {
            entity.mode = SpectracyberConfig.Mode.valueOf(mode)
            entity.integrationTime = integrationTime
            entity.offsetVoltage = offsetVoltage
            entity.IFGain = IFGain
            entity.DCGain = DCGain
            entity.bandwidth = bandwidth

            return entity
        }
    }
}