package com.msagi.recordtagger.restapi.tags

import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test

class ReadTagsTest : RestApiTest() {

    @Before
    override fun before() {
        super.before()

        for (i in 1..10) {
            repository.addTag("tagValue$i")
        }
    }

    @Test
    fun testReadTagsNoSelectedSuccess() {
        given {
            param("selected","")
            contentType(ContentType.JSON)
        } `when` {
            get("/tags")
        } then {
            body("total", equalTo(10))
            statusCode(200)
        }
    }

    @Test
    fun testReadTagsSuccess() {
        given {
            param("selected","tagValue1,tagValue2")
            contentType(ContentType.JSON)
        } `when` {
            get("/tags")
        } then {
            body("selected[0].tag", equalTo("tagValue1"))
            body("selected[1].tag", equalTo("tagValue2"))
            body("tags[0]._links[\"records\"].href", equalTo("/records?tags=tagValue2"))
            body("tags[1]._links[\"records\"].href", equalTo("/records?tags=tagValue1,tagValue2,tagValue10"))
            statusCode(200)
        }
    }

    @Test
    fun testReadTagsUnknownParametersIgnoredSuccess() {
        given {
            param("nonExistingParam", "invalidValue")
            contentType(ContentType.JSON)
        } `when` {
            get("/tags")
        } then {
            statusCode(200)
        }
    }

}