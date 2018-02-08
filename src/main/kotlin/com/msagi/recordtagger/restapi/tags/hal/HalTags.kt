package com.msagi.recordtagger.restapi.tags.hal

import com.msagi.recordtagger.repository.Tag

data class GetTagsResponse(val selected: List<Tag>, val tags: List<HalTag>, val total: Int)