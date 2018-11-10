package com.radiotelescope.repository.profilePicture

import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Spring Repository for the [ProfilePicture] Entity
 */
interface IProfilePictureRepository : PagingAndSortingRepository<ProfilePicture, Long>