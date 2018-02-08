package com.msagi.recordtagger.restapi.tags

import com.msagi.recordtagger.restapi.RestApiTest
import org.hamcrest.CoreMatchers
import org.junit.Test

class CreateTagTest : RestApiTest() {

    @Test
    fun testCreateTagSuccess() {
        val tagValue = "tagValue"
        given {
            body("tag=$tagValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            post("/tags")
        } then {
            body("tag", CoreMatchers.equalTo(tagValue))
            statusCode(201)
        }
    }

    @Test
    fun testCreateTagMultipleTimeReturnsTheFirstTagSuccess() {
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        given {
            body("tag=$tagValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            post("/tags")
        } then {
            body("id", CoreMatchers.equalTo(tag.id))
            body("tag", CoreMatchers.equalTo(tag.tag))
            statusCode(201)
        }
    }

    @Test
    fun testCreateTagInvalidBadRequest() {
        given {
            emptyBody()
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            post("/tags")
        } then {
            statusCode(400)
        }
    }
}
