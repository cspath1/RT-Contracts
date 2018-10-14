package com.radiotelescope.repository.telescope

import javax.persistence.*

/**
 * Entity Class representing a Radio Telescope. This is used with
 * scheduling appointments. This is information that will be
 * updated by the control room software, and will not be modified by
 * our application
 *
 * This Entity correlates to the Telescope SQL Table
 */
@Entity
@Table(name = "telescope")
class Telescope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private var id: Long = 0

    @Column(name = "online")
    private var online: Boolean = true

    fun getId(): Long {
        return id
    }

    fun getOnline(): Boolean {
        return online
    }
}