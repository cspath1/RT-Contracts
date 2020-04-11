package com.radiotelescope.repository.frontpagePicture

import javax.persistence.*

/**
 * Entity Class representing a Frontpage Picture for the web-application
 *
 * This Entity correlates to the Frontpage Picture SQL table
 */
@Entity
@Table(name = "frontpage_picture")
class FrontpagePicture(
        @Column(name = "picture_title", nullable = false)
        var pictureTitle: String,
        @Column(name = "picture_url", nullable = false)
        var pictureUrl: String,
        @Column(name = "description", nullable = false)
        var description: String,
        @Column(name = "approved")
        var approved: Boolean = false
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0
}