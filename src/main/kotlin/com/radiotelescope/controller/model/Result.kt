package com.radiotelescope.controller.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializable
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import org.springframework.http.HttpStatus

/**
 * Class used to send information back to the client
 *
 * @property data the data returned on success
 * @property errors a map of any errors
 * @property status the associated [HttpStatus]
 */
class Result(
        var data: Any? = null,
        var errors: Map<String, Collection<String>>? = null,
        var status: HttpStatus? = null
) : JsonSerializable {
    init {
        if (status == null) status = if (errors == null) HttpStatus.OK
        else HttpStatus.BAD_REQUEST
    }

    override fun serialize(gen: JsonGenerator?, serializers: SerializerProvider?) {
        if (gen != null) {
            gen.writeStartObject()

            gen.writeStringField("statusCode", status?.value().toString())
            gen.writeStringField("statusReason", status?.reasonPhrase)

            if (data != null) {
                gen.writeObjectField("data", data)
            }

            if (errors != null && errors!!.isNotEmpty())
                gen.writeObjectField("errors", errors)

            gen.writeEndObject()
        }
    }

    override fun serializeWithType(gen: JsonGenerator?, serializers: SerializerProvider?, typeSer: TypeSerializer?) {
        serialize(gen, serializers)
    }
}