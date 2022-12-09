package com.radiotelescope.repository.spectracyberConfig

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [SpectracyberConfig] Entity
 */
@Repository
interface ISpectracyberConfigRepository: CrudRepository<SpectracyberConfig, Long>