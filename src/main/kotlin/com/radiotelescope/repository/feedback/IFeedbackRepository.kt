package com.radiotelescope.repository.feedback

import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Spring Repository for the [Feedback] Entity
 */
interface IFeedbackRepository : PagingAndSortingRepository<Feedback, Long>