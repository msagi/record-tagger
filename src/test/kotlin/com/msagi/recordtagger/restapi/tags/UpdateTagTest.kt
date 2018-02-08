package com.msagi.recordtagger.restapi.tags

import com.msagi.recordtagger.restapi.RestApiTest
import org.hamcrest.CoreMatchers
import org.junit.Test

class UpdateTagTest : RestApiTest() {

    @Test
    fun testUpdateTagSuccess() {
        val tagValue = "tagValue"
        val tagValue2 = "tagValue2"
        val tag = repository.addTag(tagValue)
        given {
            body("tag=$tagValue2")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/tags/${tag.id}")
        } then {
            body("tag", CoreMatchers.equalTo(tagValue2))
            statusCode(201)
        }
    }

    @Test
    fun testUpdateInvalidTagBadRequest() {
        val tagValue = "tagValue"
        val tag = repository.addTag(tagValue)
        given {
            emptyBody()
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/tags/${tag.id}")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testUpdateInvalidTagIdBadRequest() {
        val tagValue = "tagValue"
        given {
            body("tag=$tagValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/tags/thisIsNotANumber")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testUpdateNonExistingTagNotFound() {
        val tagValue = "tagValue"
        given {
            body("tag=$tagValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            put("/tags/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

}
