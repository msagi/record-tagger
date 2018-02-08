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

package com.msagi.recordtagger.restapi.tags.hal

import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinks

data class HalTag(val _links: HalLinks, val id: Int, val tag: String)

fun Tag.toHalTag() = HalTag(HalLinks(self = HalLink(href = "/tags/$id")), id, tag)

fun Tag.toHalTag(links: HalLinks) = HalTag(links, id, tag)
