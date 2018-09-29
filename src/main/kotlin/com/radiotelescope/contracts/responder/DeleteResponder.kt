package com.example.project.contract.responder

import com.google.common.collect.HashMultimap;

interface DeleteResponder<T>: Responder<Long, HashMultimap<T, String>>

