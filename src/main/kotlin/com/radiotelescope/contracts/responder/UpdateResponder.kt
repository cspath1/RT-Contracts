package com.example.project.contract.responder

import com.google.common.collect.Multimap

interface UpdateResponder<T> : Responder<Long, Multimap<T, String>>