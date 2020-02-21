//package com.radiotelescope.contracts.thresholds
//
//import com.google.common.collect.HashMultimap
//import com.google.common.collect.Multimap
//import com.radiotelescope.contracts.Command
//import com.radiotelescope.contracts.SimpleResult
//import com.radiotelescope.repository.thresholds.IThresholdsRepository
//import com.radiotelescope.repository.thresholds.Thresholds
//
///**
// * Override of the [Command] interface method used to retrieve [Thresholds]
// * information
// *
// * @param thresholdsRepo the [IThresholdsRepository] interface
// */
//class Update (
//        private val request: Request,
//        private val thresholdsRepo: IThresholdsRepository,
//        private val sensorName: Thresholds.Name
//) : Command<Thresholds, Multimap<ErrorTag, String>> {
//
//    /**
//     * Override of the [Command] execute method. It checks the database for
//     * the single entry in the thresholds table using the [IThresholdsRepository.findAll]
//     * method.
//     *
//     * If the thresholds entry does not exist (should never happen),
//     * it will return an error in the [SimpleResult].
//     */
//    override fun execute(): SimpleResult<Thresholds, Multimap<ErrorTag, String>> {
//        val errors = validateRequest()
//
//        if (!errors.isEmpty)
//            return SimpleResult(null, errors)
//
//        val sensorThreshold = thresholdsRepo.getMostRecentThresholdByName(request.sensorName)
//        sensorThreshold.maximum = request.maximum
//
//        return SimpleResult(thresholdsRepo.save(sensorThreshold), null)
//    }
//
//    private fun validateRequest(): Multimap<ErrorTag, String>{
//        val errors = HashMultimap.create<ErrorTag, String>()
//        val sensorThreshold = thresholdsRepo.getMostRecentThresholdByName(request.sensorName)
//
//        with(request) {
//            // should never happen
//            if (thresholdsRepo.findAll().count() == 0)
//                errors.put(ErrorTag.ID, "Sensor thresholds not found")
//
//            // check if each threshold below zero
//            if(sensorName != null && maximum < 0)
//                errors.put(ErrorTag.NAME, "Threshold must be higher than 0")
//        }
//
//        return errors
//    }
//
//    /**
//     * @param SensorName the name of the sensor in the thresholds table
//     */
//    data class Request(
//            val sensorName: Thresholds.Name,
//            val maximum: Double
//    )
//}