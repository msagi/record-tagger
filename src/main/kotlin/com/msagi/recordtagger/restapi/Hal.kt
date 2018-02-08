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