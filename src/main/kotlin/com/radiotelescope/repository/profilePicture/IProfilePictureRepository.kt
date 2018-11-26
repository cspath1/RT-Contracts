package com.radiotelescope.repository.profilePicture

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Spring Repository for the [ProfilePicture] Entity
 */
interface IProfilePictureRepository : PagingAndSortingRepository<ProfilePicture, Long> {
    @Query(value = "SELECT * " +
            "FROM profile_picture " +
            "WHERE user_id =?1 " +
            "AND validated = 1 " +
            "ORDER BY id ASC " +
            "LIMIT 1",
            nativeQuery = true)
    fun findUsersActiveProfilePicture(userId: Long): ProfilePicture
}