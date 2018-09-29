package.com.radiotelescope.contract.responder

interface Responder<in SUCCESS_CALLBACK_TYPE, in FAILURE_CALLBACK_TYPE>
{
    fun onSuccess(t: SUCCESS_CALLBACK_TYPE)

    fun onFailure(e: FAILURE_CALLBACK_TYPE)
}