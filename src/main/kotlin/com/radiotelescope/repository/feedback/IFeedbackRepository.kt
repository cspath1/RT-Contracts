package com.radiotelescope.repository.feedback

import org.springframework.data.repository.CrudRepository

/**
 * Spring Repository for the [Feedback] Entity
 */
interface IFeedbackRepository : CrudRepository<Feedback, Long>