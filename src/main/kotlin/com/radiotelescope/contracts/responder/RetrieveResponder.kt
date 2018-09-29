package com.example.project.contract.responder

import com.google.common.collect.Multimap

interface RetrieveResponder<in T, ERROR_TAG> : Responder<T, Multimap<ERROR_TAG, String>>

