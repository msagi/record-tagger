package com.msagi.recordtagger.restapi.records.hal

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinks

fun List<Record>.toHalRecords() : List<HalRecord> = map { it.toHalRecord() }

fun Record.toHalRecord() = HalRecord(HalLinks(self = HalLink(href = "/records/$id")), id, record, tags)

data class HalRecord(val _links: HalLinks, val id: Int, val record: String, val tags: List<Tag>)
