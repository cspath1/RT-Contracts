package com.radiotelescope.contracts.user

import com.google.common.collect.Multimap
import com.radiotelescope.contracts.Command
import com.radiotelescope.repository.accountActivateToken.IAccountActivateTokenRepository
import com.radiotelescope.repository.allottedTimeCap.IAllottedTimeCapRepository
import com.radiotelescope.repository.loginAttempt.ILoginAttemptRepository
import com.radiotelescope.repository.model.user.SearchCriteria
import com.radiotelescope.repository.role.IUserRoleRepository
import com.radiotelescope.repository.user.IUserRepository
import com.radiotelescope.repository.user.User
import com.radiotelescope.service.s3.IAwsS3DeleteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Base concrete implementation of the [UserFactory] interface
 *
 * @param userRepo the [IUserRepository] interface
 * @param userRoleRepo the [IUserRoleRepository] interface
 * @param accountActivateTokenRepo the [IAccountActivateTokenRepository] interface
 * @param allottedTimeCapRepo the [IAllottedTimeCapRepository] interface
 * @param loginAttemptRepo the [ILoginAttemptRepository] interface
 */
class BaseUserFactory(
        private val userRepo: IUserRepository,
        private val userRoleRepo: IUserRoleRepository,
        private val accountActivateTokenRepo: IAccountActivateTokenRepository,
        private val allottedTimeCapRepo: IAllottedTimeCapRepository,
        private val loginAttemptRepo: ILoginAttemptRepository,
        private val deleteService: IAwsS3DeleteService
) : UserFactory {
    /**
     * Override of the [UserFactory.register] method that will return a [Register] command object
     *
     * @param request the [Register.Request] object
     * @return a [Register] command object
     */
    override fun register(request: Register.Request): Command<Register.Response, Multimap<ErrorTag, String>> {
        return Register(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserFactory.authenticate] method that will return a [Authenticate] command object
     *
     * @param request the [Authenticate.Request] object
     * @return a [Authenticate] command object
     */
    override fun authenticate(request: Authenticate.Request): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Authenticate(
                request = request,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo,
                loginAttemptRepo = loginAttemptRepo
        )
    }

    /**
     * Override of the [UserFactory.retrieve] method that will return a [Retrieve] command object
     *
     * @param id the User id
     * @return a [Retrieve] command object
     */
    override fun retrieve(id: Long): Command<UserInfo, Multimap<ErrorTag, String>> {
        return Retrieve(
                id = id,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserFactory.list] method that will return a [List] command object
     *
     * @param pageable the [Pageable] request
     * @return a [List] command object
     */
    override fun list(pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
        return List(
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserFactory.update] method that will return a [Update] command object
     *
     * @param request the [Update.Request] object
     * @return a [Update] command object
     */
    override fun update(request: Update.Request): Command<Long, Multimap<ErrorTag, String>> {
        return Update(
                request = request,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.updateProfilePicture] method that will return a
     * [UpdateProfilePicture] command object
     *
     * @param request the [UpdateProfilePicture.Request] object
     * @return a [UpdateProfilePicture] command object
     */
    override fun updateProfilePicture(request: UpdateProfilePicture.Request): Command<Long, Multimap<ErrorTag, String>> {
        return UpdateProfilePicture(
                request = request,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.approveDenyProfilePicture] method that will
     * return an [ApproveDeny] command object
     *
     * @param request the [ApproveDeny.Request] object
     * @return an [ApproveDeny] command object
     */
    override fun approveDenyProfilePicture(request: ApproveDeny.Request): Command<User, Multimap<ErrorTag, String>> {
        return ApproveDeny(
                request = request,
                userRepo = userRepo,
                deleteService = deleteService
        )
    }

    /**
     * Override of the [UserFactory.delete] method that will return a [Delete] command object
     *
     * @param id the User id
     * @return a [Delete] command object
     */
    override fun delete(id: Long): Command<Long, Multimap<ErrorTag, String>> {
        return Delete(
                id = id,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.ban] method that will return a [Ban] command object
     *
     * @param id the User id
     * @return a [Ban] command object
     */
    override fun ban(id: Long): Command<Long, Multimap<ErrorTag, String>> {
        return Ban(
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                id = id
        )
    }

    /**
     * Override of the [UserFactory.unban] method that will return a [Unban] command object
     *
     * @param [Unban.Response] object
     * @return an [Unban] command object
     */
    override fun unban(id: Long): Command<Unban.Response, Multimap<ErrorTag, String>> {
        return Unban(
                id = id,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.changePassword] method that will return a [ChangePassword]
     * command object
     *
     * @param request the [ChangePassword.Request]
     * @return a [ChangePassword] command object
     */
    override fun changePassword(request: ChangePassword.Request): Command<Long, Multimap<ErrorTag, String>> {
        return ChangePassword(
                request = request,
                userRepo = userRepo
        )
    }

    /**
     * Override of the [UserFactory.search] method that will return a [Search] command object
     *
     * @return a [Search] command object
     */
    override fun search(searchCriteria: kotlin.collections.List<SearchCriteria>, pageable: Pageable): Command<Page<UserInfo>, Multimap<ErrorTag, String>> {
        return Search(
                searchCriteria = searchCriteria,
                pageable = pageable,
                userRepo = userRepo,
                userRoleRepo = userRoleRepo,
                allottedTimeCapRepo = allottedTimeCapRepo
        )
    }

    /**
     * Override of the [UserFactory.invite] method that will return an [Invite] command object
     *
     * @return an [Invite] command object
     */
    override fun invite(email: String): Command<Boolean, Multimap<ErrorTag, String>>{
        return Invite(
                email = email,
                userRepo = userRepo
        )
    }
}