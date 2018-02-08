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

package com.msagi.recordtagger.restapi.records

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.withClassOf
import com.msagi.recordtagger.RequestHandler
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalEmbedded
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinkType
import com.msagi.recordtagger.restapi.HalLinks
import com.msagi.recordtagger.restapi.records.hal.HalRecords
import com.msagi.recordtagger.restapi.records.hal.toHalRecord
import com.msagi.recordtagger.restapi.records.hal.toHalRecords
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.ValuesMap
import org.slf4j.Logger

const val KEY_RECORD_ID = "recordId"
const val KEY_TAG_ID = "tagId"
const val KEY_TAG = "tag"
const val KEY_RECORD = "record"

/**
 * RESTful API for record management. See API.md for details.
 */
class RecordsRestApi(kodein: Kodein) : RequestHandler {

    private val maxLimit: Int = kodein.instance("recordsPageSize")

    private val repository: Repository = kodein.instance()

    private val logger: Logger = kodein.withClassOf(this).instance()

    private fun getRecordsHalLinks(tags: List<Tag>, limit: Int, offset: Int, total: Int): HalLinks {
        val tagsParameter = tags.joinToString(",") { it.tag }
        val links = HalLinks(self = HalLink(href = getRecordsLink(tagsParameter, limit, offset)))
        if (offset > 0) {
            links[HalLinkType.first] = HalLink(href = getRecordsLink(tagsParameter, limit, 0))
            //this can be negative here but offset verification corrects it
            links[HalLinkType.prev] = HalLink(href = getRecordsLink(tagsParameter, limit, offset - limit))
        }
        if (offset + limit < total) {
            links[HalLinkType.next] = HalLink(href = getRecordsLink(tagsParameter, limit, offset + limit))
            val lastOffset = when {
                total - limit < 0 -> 0
                else -> total - limit
            }
            links[HalLinkType.last] = HalLink(href = getRecordsLink(tagsParameter, limit, lastOffset))
        }
        links["tags"] = HalLink(href = getTagsLink(tagsParameter))
        return links
    }

    private fun getRecordsLink(tags: String, limit: Int, offset: Int): String = "/records?tags=$tags&limit=$limit&offset=$offset"

    private fun getTagsLink(tags: String): String = "/tags?selected=$tags"

    override fun getHandler(): Routing.() -> Unit = {
        post("/records") {
            //create new record
            try {
                val post = call.receiveOrNull<ValuesMap>()
                val recordValue: String? = post?.get(KEY_RECORD)
                if (recordValue == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val record = repository.addRecord(recordValue)
                val halRecord = record.toHalRecord()
                call.respond(HttpStatusCode.Created, halRecord)
            } catch (e: Exception) {
                logger.error("Error creating new record", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        get("/records/{recordId}") {
            // read record by its ID
            val id: Int? = call.parameters[KEY_RECORD_ID]?.toIntOrNull()
            try {
                id?.let {
                    repository.getRecord(it)?.let {
                        call.respond(HttpStatusCode.OK, it.toHalRecord())
                        return@get
                    }
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                logger.error("Error getting record by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        put("/records/{recordId}") {
            // update record by its ID
            val id: Int? = call.parameters[KEY_RECORD_ID]?.toIntOrNull()
            try {
                val post = call.receiveOrNull<ValuesMap>()
                val data: String? = post?.get(KEY_RECORD)
                if (id != null && data != null) {
                    repository.setRecord(id, data).let {
                        call.respond(HttpStatusCode.Created, it.toHalRecord())
                        return@put
                    }
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                logger.error("Error updating record by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        delete("/records/{recordId}") {
            // delete record by its ID
            val id = call.parameters[KEY_RECORD_ID]?.toIntOrNull()
            try {
                id?.let {
                    repository.removeRecord(it)
                    call.respond(HttpStatusCode.NoContent)
                    return@delete
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                logger.error("Error deleting record by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        get("/records") {
            // get all records, filtered, paginated
            try {
                val tags: List<String>? = call.request.queryParameters["tags"]?.split(",")?.map { it.trim() }
                val verifiedTags = when {
                    tags == null || tags.isEmpty() -> listOf()
                    else -> repository.getTagsByValues(tags)
                }

                val limit: Int? = call.request.queryParameters["limit"]?.toIntOrNull()
                val verifiedLimit = when {
                    limit == null || limit <= 0 || limit > maxLimit -> maxLimit
                    else -> limit
                }

                val offset: Int? = call.request.queryParameters["offset"]?.toIntOrNull()
                val verifiedOffset = when {
                    offset == null || offset < 0 -> 0
                    else -> offset
                }

                val totalRecordsFound = repository.getNumberOfRecords(verifiedTags)
                val halLinks = getRecordsHalLinks(verifiedTags, verifiedLimit, verifiedOffset, totalRecordsFound)
                val halRecords = repository.getRecords(verifiedTags, verifiedLimit, verifiedOffset).toHalRecords()
                val halEmbedded = HalEmbedded()
                halEmbedded["records"] = halRecords

                //TODO decide to set response content type to application/hal+json to follow IETF draft?
                call.respond(HttpStatusCode.OK, HalRecords(halLinks, halEmbedded, totalRecordsFound))
            } catch (e: Exception) {
                logger.error("Error reading records", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post("/records/{recordId}/tags/{tagId}") {
            //tag record
            val recordId: Int? = call.parameters[KEY_RECORD_ID]?.toIntOrNull()
            val tagId: Int? = call.parameters[KEY_TAG_ID]?.toIntOrNull()
            try {
                if (recordId != null && tagId != null) {
                    val record = repository.tag(recordId, tagId)
                    call.respond(HttpStatusCode.Created, record.toHalRecord())
                    return@post
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                logger.error("Error tagging record: recordId: $recordId, tagId: $tagId", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        delete("/records/{recordId}/tags/{tagId}") {
            //untag record
            val recordId: Int? = call.parameters[KEY_RECORD_ID]?.toIntOrNull()
            val tagId: Int? = call.parameters[KEY_TAG_ID]?.toIntOrNull()
            try {
                if (recordId != null && tagId != null) {
                    val record = repository.untag(recordId, tagId)
                    call.respond(HttpStatusCode.OK, record.toHalRecord())
                    return@delete
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                logger.error("Error untagging record: recordId: $recordId, tagId: $tagId", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

    }
}