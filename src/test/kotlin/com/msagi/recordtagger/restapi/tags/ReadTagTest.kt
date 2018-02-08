package com.msagi.recordtagger.restapi.tags

import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test

class ReadTagTest : RestApiTest() {

    val tags = mutableListOf<Tag>()

    @Before
    override fun before() {
        super.before()

        for (i in 0 until 10) tags.add(repository.addTag("tagValue$i"))
    }

    @Test
    fun testReadTagSuccess() {
        given {
            contentType(ContentType.JSON)
        } `when` {
            get("/tags/${tags[3].id}")
        } then {
            body("id", equalTo(tags[3].id))
            statusCode(200)
        }
    }

    @Test
    fun testReadNonExistingTagNotFound() {
        given {
            contentType(ContentType.JSON)
        } `when` {
            get("/tags/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

}