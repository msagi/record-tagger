/*
 * Copyright 2018 Miklos Sagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msagi.recordtagger.restapi

import java.util.HashMap

/**
 * Hypertext Application Language Links. See https://tools.ietf.org/html/draft-kelly-json-hal-08
 */

class HalLinks constructor(
        self: HalLink,
        curies: HalLink? = null,
        prev: HalLink? = null,
        next: HalLink? = null,
        first: HalLink? = null,
        last: HalLink? = null) : HashMap<String, HalLink>()  {
    init {
        safePut(HalLinkType.self, self)
        safePut(HalLinkType.curies, curies)
        safePut(HalLinkType.prev, prev)
        safePut(HalLinkType.next, next)
        safePut(HalLinkType.first, first)
        safePut(HalLinkType.last, last)
    }
    private fun safePut(key: String, value: HalLink?) { value?.let { put(key, value) } }
}

class HalLinkType {
    companion object {
        const val self = "self"
        const val curies = "curies"
        const val prev = "prev"
        const val next = "next"
        const val first = "first"
        const val last = "last"
    }
}

data class HalLink(
        var href: String,
        var templated: String? = null,
        var type: String? = null,
        var deprecation: String? = null,
        var name: String? = null,
        var hreflang: String? = null)

class HalEmbedded : HashMap<String, Any>()