package com.msagi.recordtagger.restapi.records.hal

import com.msagi.recordtagger.restapi.HalEmbedded
import com.msagi.recordtagger.restapi.HalLinks

data class HalRecords(val _links: HalLinks, val _embedded: HalEmbedded, val total: Int)