package com.msagi.recordtagger.restapi.tags.hal

import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinks

data class HalTag(val _links: HalLinks, val id: Int, val tag: String)

fun Tag.toHalTag() = HalTag(HalLinks(self = HalLink(href = "/tags/$id")), id, tag)

fun Tag.toHalTag(links: HalLinks) = HalTag(links, id, tag)
