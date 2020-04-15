package com.radiotelescope.repository.frontpagePicture

import com.radiotelescope.repository.videoFile.VideoFile
import org.springframework.data.domain.Page
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [FrontpagePicture] Entity.
 */
@Repository

interface IFrontpagePictureRepository : PagingAndSortingRepository<FrontpagePicture, Long> {

    /**
     * Spring Repository method that will return all [FrontpagePicture] records
     * that are approved
     *
     * @return a [Page] of [FrontpagePicture]
     */
    @Query(value = "SELECT * " +
            "FROM frontpage_picture " +
            "WHERE approved = true",
            nativeQuery = true
    )
    fun findAllApproved(): List<FrontpagePicture>
}