package com.example.project.contracts.appointment

import com.radiotelescope.contract.Command
import com.radiotelescope.contract.responder.CreateResponder
import com.radiotelescope.contract.responder.DeleteResponder
import com.radiotelescope.contract.responder.RetrieveResponder
import com.radiotelescope.contract.responder.UpdateResponder
import com.radiotelescope.repository.appointment.IAppointmentRepository

class BaseAppointmentFactory(
    private val apptRepo: AppointmentRepository


) : AppointmentFactory {
    override fun retrieve(id: Long, responder: RetrieveResponder<AppointmentInfo, ErrorTag>): Command {
        return Retrieve(id, responder, apptRepo)

    }

    override fun retrieve(request: Validate.Request, responder: RetrieveResponder<AppointmentInfo, ErrorTag>): Command {

        return Validate(request, responder, apptRepo)


    }

    override fun create(request: Create.Request, responder: CreateResponder<ErrorTag>): Command {

        return Create(request, responder, apptRepo)

    }

    override fun update(request: Update.Request, responder: UpdateResponder<ErrorTag>): Command {
        return Update(request, responder, apptRepo)
    }

    override fun delete(id: Long, responder: DeleteResponder<ErrorTag>): Command {
        return Delete(id, responder, apptRepo)
    }

}

