package com.radiotelescope.repository.thresholds

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [Thresholds] Entity
 */
@Repository
interface IThresholdsRepository: CrudRepository<Thresholds, Long> {
}