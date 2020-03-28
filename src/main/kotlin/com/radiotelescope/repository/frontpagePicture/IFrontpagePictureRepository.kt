package com.radiotelescope.repository.frontpagePicture

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

/**
 * Spring Repository for the [FrontpagePicture] Entity.
 */
@Repository
interface IFrontpagePictureRepository : PagingAndSortingRepository<FrontpagePicture, Long> {
}