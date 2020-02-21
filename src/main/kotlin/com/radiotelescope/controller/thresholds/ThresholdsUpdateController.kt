//package com.radiotelescope.controller.thresholds
//
//import com.radiotelescope.contracts.thresholds.UserThresholdsWrapper
//import com.radiotelescope.contracts.thresholds.Update
//import com.radiotelescope.controller.BaseRestController
//import com.radiotelescope.controller.model.Result
//import com.radiotelescope.controller.spring.Logger
//import com.radiotelescope.repository.log.Log
//import com.radiotelescope.security.AccessReport
//import com.radiotelescope.toStringMap
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.http.HttpStatus
//import org.springframework.web.bind.annotation.*
//
///**
// * Rest Controller to handle Appointment retrieval
// *
// * @param thresholdsWrapper the [UserThresholdsWrapper]
// * @param logger the [Logger] service
// */
//@RestController
//class ThresholdsUpdateController(
//        @Qualifier(value = "thresholdsWrapper")
//        private val thresholdsWrapper: UserThresholdsWrapper,
//        logger: Logger
//) : BaseRestController(logger) {
//    /**
//     * Execute method that is in charge of adapting the [CreateForm]
//     * into a [Create.Request] after ensuring no fields are null. If
//     * any are, it will instead respond with errors.
//     *
//     * Otherwise, it will execute the [UserWeatherDataWrapper.create] method.
//     * Based on the result of this call, the method will either respond with
//     * the data or the errors from the method call.
//     */
//    @PostMapping(value = ["/api/thresholds"])
//    fun execute(@RequestBody request: Update.Request): Result {
//        // If the request validation fails, respond with errors
//        Update.validateRequest()?.let {
//            // Create error logs
//            logger.createErrorLogs(
//                    info = Logger.createInfo(
//                            affectedTable = Log.AffectedTable.THRESHOLDS,
//                            action = "Sensor threshold maximum change",
//                            affectedRecordId = null,
//                            status = HttpStatus.BAD_REQUEST.value()
//                    ),
//                    errors = it.toStringMap()
//            )
//
//            result = Result(errors = it.toStringMap())
//        }?:
//        // Otherwise, execute the wrapper command
//        let {
//            val response = thresholdsWrapper.update(
//                    request = request.toRequest()
//            ).execute()
//            // If the command was a success
//            response.success?.let { data ->
//                // Create success log
//                logger.createSuccessLog(
//                        info = Logger.createInfo(
//                                affectedTable = Log.AffectedTable.THRESHOLDS,
//                                action = "Sensor threshold maximum change",
//                                affectedRecordId = data,
//                                status = HttpStatus.OK.value()
//                        )
//                )
//                result = Result(data = data)
//            }
//            // Otherwise, there was an error
//            response.error?.let { errors ->
//                // Create error logs
//                logger.createErrorLogs(
//                        info = Logger.createInfo(
//                                affectedTable = Log.AffectedTable.THRESHOLDS,
//                                action = "Sensor threshold maximum change",
//                                affectedRecordId = null,
//                                status = HttpStatus.BAD_REQUEST.value()
//                        ),
//                        errors = errors.toStringMap()
//                )
//
//                result = Result(errors = errors.toStringMap())
//            }
//        }
//        return result
//    }
//}