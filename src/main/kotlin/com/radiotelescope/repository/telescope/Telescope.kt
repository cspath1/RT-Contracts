package com.radiotelescope.repository.telescope

import javax.persistence.*

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