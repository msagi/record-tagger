package com.msagi.recordtagger.restapi.tags

import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test

class DeleteTagTest : RestApiTest() {

    private val tags = mutableListOf<Tag>()

    @Before
    override fun before() {
        super.before()

        for (i in 0 until 10) tags.add(repository.addTag("tagValue$i"))
    }

    @Test
    fun testDeleteTagSuccess() {
        given {
            emptyBody()
        } `when` {
            delete("/tags/${tags[3].id}")
        } then {
            statusCode(204)
        }
    }

    @Test
    fun testDeleteTagInvalidBadRequest() {
        given {
            emptyBody()
        } `when` {
            delete("/tags/thisIsNotANumber")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testDeleteNonExistingTagNotFound() {
        given {
            emptyBody()
        }`when` {
            delete("/tags/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

}