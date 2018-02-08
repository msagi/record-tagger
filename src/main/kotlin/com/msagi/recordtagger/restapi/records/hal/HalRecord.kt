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

package com.msagi.recordtagger.restapi.records.hal

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinks

fun List<Record>.toHalRecords() : List<HalRecord> = map { it.toHalRecord() }

fun Record.toHalRecord() = HalRecord(HalLinks(self = HalLink(href = "/records/$id")), id, record, tags)

data class HalRecord(val _links: HalLinks, val id: Int, val record: String, val tags: List<Tag>)
