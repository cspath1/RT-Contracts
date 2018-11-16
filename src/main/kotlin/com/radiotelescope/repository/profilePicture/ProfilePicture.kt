package com.radiotelescope.repository.profilePicture

import com.radiotelescope.repository.user.User
import javax.persistence.*

/**
 * Entity Class representing a Profile Picture for a user
 *
 * This Entity correlates to the Profile Picture SQL Table
 */
@Entity
@Table(name = "profile_picture")
data class ProfilePicture(
        @Column(name = "profile_picture_url")
        var profilePictureUrl: String,
        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    var id: Long = 0

    @Column(name = "validated")
    var validated: Boolean = false

    companion object {
        // Default profile picture that will be used if the user has not
        // uploaded a profile picture
        const val DEFAULT_PROFILE_PICTURE = "https://s3.us-east-2.amazonaws.com/ycpradiotelescope/default_profile_picture.png"
    }
}