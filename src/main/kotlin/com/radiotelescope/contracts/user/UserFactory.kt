package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.repository.user.User
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.model.user.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import kotlin.collections.List

/**
 * Abstract factory interface with methods for all [User] CRUD operations.
 */
interface UserFactory {
    /**
     * Abstract command used to register a user for the site
     *
     * @param request the [Register.Request] request
     * @return a [Command] object
     */
    fun register(request: Register.Request): Command<Register.Response, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to log a user in to the site
     *
     * @param request the [Authenticate.Request] request
     * @return a [Command] object
     */
    fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to retrieve the user's information
     *
     * @param id the [User] id
     * @return a [Command] object
     */
    fun retrieve(id: Long): Command<UserInfo, Multimap<ErrorTag,String>>

    /**
     * Abstract command used by admins to retrieve a [Page] of [UserInfo]
     *
     * @param pageable the [Pageable] object
     * @return a [Command] object
     */
    fun list(pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update the user's information
     *
     * @param request the [Update.Request] request
     * @return a [Command] object
     */
    fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update the user's information
     *
     * @param request the [UpdateProfilePicture.Request] request
     * @return a [Command] object
     */
    fun updateProfilePicture(request: UpdateProfilePicture.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to update the user's information
     *
     * @param request the [ApproveDeny.Request] request
     * @return a [Command] object
     */
    fun approveDenyProfilePicture(request: ApproveDeny.Request): Command<User, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to "delete" a user's account
     *
     * @param id the User id
     * @return a [Command] object
     */
    fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to ba a user's account
     *
     * @param id the User id
     * @return a [Command] object
     */
    fun ban(id: Long): Command<Long, Multimap<ErrorTag, String>>
    /**
     * Abstract command used to unban a user's account
     *
     * @param [Unban.Response] object
     * @return a [Command] object
     */
    fun unban(id: Long): Command<Unban.Response, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to change a user's password
     *
     * @param request the [ChangePassword.Request] request
     * @return a [Command] object
     */
    fun changePassword(request: ChangePassword.Request): Command<Long, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to search for users
     *
     * @param searchCriteria a [List] of [SearchCriteria]
     * @param pageable the [Pageable] object
     * @return a [Command] object
     */
    fun search(searchCriteria: List<SearchCriteria>, pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>>

    /**
     * Abstract command used to invite users by email
     *
     * @param email the email address to send an invite to
     */
    fun invite(email: String): Command<Boolean, Multimap<ErrorTag, String>>
}