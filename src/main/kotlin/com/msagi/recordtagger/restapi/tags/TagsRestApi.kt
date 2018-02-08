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

package com.msagi.recordtagger.restapi.tags

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.withClassOf
import com.msagi.recordtagger.RequestHandler
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.HalLink
import com.msagi.recordtagger.restapi.HalLinks
import com.msagi.recordtagger.restapi.records.KEY_TAG
import com.msagi.recordtagger.restapi.records.KEY_TAG_ID
import com.msagi.recordtagger.restapi.tags.hal.GetTagsResponse
import com.msagi.recordtagger.restapi.tags.hal.HalTag
import com.msagi.recordtagger.restapi.tags.hal.toHalTag
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import org.slf4j.Logger

/**
 * RESTful API for tag management. See API.md for details.
 */
class TagsRestApi(kodein: Kodein) : RequestHandler {

    private val repository: Repository = kodein.instance()

    private val logger: Logger = kodein.withClassOf(this).instance()

    private fun getHalTags(tags: List<Tag>, selectedTags: List<Tag>): List<HalTag> = tags.map {
        val halTag = it.toHalTag()
        when (it) {
            in selectedTags -> {
                halTag._links["records"] = HalLink(href = "/records?tags=${selectedTags.minus(it).joinToString(",") { tag -> tag.tag }}")
            }
            else -> {
                halTag._links["records"] = HalLink(href = "/records?tags=${selectedTags.plus(it).joinToString(",") { tag -> tag.tag }}")
            }
        }
        halTag
    }

    override fun getHandler(): Routing.() -> Unit = {
        get("/tags") {
            //get all tags
            val selectedTags: List<String>? = call.request.queryParameters["selected"]?.split(",")?.map { it.trim() }
            try {
                val verifiedSelectedTags = when {
                    selectedTags == null || selectedTags.isEmpty() -> listOf()
                    else -> repository.getTagsByValues(selectedTags)
                }
                val allTags = repository.getAllTags()
                val total = allTags.size
                val halTags = getHalTags(allTags, verifiedSelectedTags)

                //TODO decide to set response content type to application/hal+json to follow IETF draft?
                call.respond(HttpStatusCode.OK, GetTagsResponse(verifiedSelectedTags, halTags, total))
            } catch (e: Exception) {
                logger.error("Error reading all tags", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post("/tags") {
            //Create new tag
            try {
                val post = call.receiveOrNull<Parameters>()
                val tagValue: String? = post?.get(KEY_TAG)
                if (tagValue == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val tag = repository.addTag(tagValue)
                val halTag = tag.toHalTag()
                call.respond(HttpStatusCode.Created, halTag)
            } catch (e: Exception) {
                logger.error("Error creating new tag", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        get("/tags/{tagId}") {
            //Read tag by its ID
            val id: Int? = call.parameters[KEY_TAG_ID]?.toIntOrNull()
            try {
                id?.let {
                    repository.getTag(it)?.let {
                        call.respond(HttpStatusCode.OK, it.toHalTag())
                        return@get
                    }
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(HttpStatusCode.BadRequest)
            } catch (e: Exception) {
                logger.error("Error getting tag by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        put("/tags/{tagId}") {
            //Update existing tag
            val id = call.parameters[KEY_TAG_ID]?.toIntOrNull()
            try {
                val post = call.receiveOrNull<Parameters>()
                val tagValue: String? = post?.get("tag")
                if (id == null || tagValue == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@put
                }
                val tag = repository.setTag(id, tagValue)
                val halTag = tag.toHalTag(HalLinks(self = HalLink(href = "/tags/${tag.id}")))
                call.respond(HttpStatusCode.Created, halTag)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound)
            } catch (e: Exception) {
                logger.error("Error updating tag by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        delete("/tags/{tagId}") {
            //Delete existing tag
            val id = call.parameters[KEY_TAG_ID]?.toIntOrNull()
            try {
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                val tag = repository.getTag(id)
                when (tag) {
                    null -> call.respond(HttpStatusCode.NotFound)
                    else -> {
                        repository.removeTag(id)
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            } catch (e: Exception) {
                logger.error("Error deleting tag by ID: id: $id", e)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}