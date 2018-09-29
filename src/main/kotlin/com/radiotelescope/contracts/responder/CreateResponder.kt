package com.example.project.contract.responder

import com.google.common.collect.Multimap;

interface CreateResponder<T>: Responder<Long, Multimap<T, String>>