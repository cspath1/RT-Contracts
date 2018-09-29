import com.radiotelescope.repository.appointment

import com.radiotelescope.contract.Command
import com.radiotelescope.contract.responder.CreateResponder
import com.radiotelescope.contract.responder.DeleteResponder
import com.radiotelescope.contract.responder.RetrieveResponder
import com.radiotelescope.contract.responder.UpdateResponder


/*
So for the Appointment entity we have the findByAppointmentId and findByUsernameId command objects



 */

interface AppointmentFactory
{


    fun create(request: Create.Request responder: CreateResponder<ErrorTag>):Command

    fun delete(id:Long, responder: DeleteResponder<ErrorTag>):Command

    fun retrieve(id:Long, responder: RetrieveResponder<UserInfo, ErrorTag>):Command

    fun retrieve(request: Validate.Request, responder: RetrieveResponder<UserInfo, ErrorTag>):Command

    fun update(request: Update.Request, responder:UpdateResponder<ErrorTag>):Command






}