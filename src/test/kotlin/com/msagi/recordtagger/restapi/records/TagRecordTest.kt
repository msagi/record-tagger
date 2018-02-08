package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.Test

class TagRecordTest : RestApiTest() {

    private lateinit var record: Record
    private lateinit var tag: Tag

    @Before
    override fun before() {
        super.before()

        record = repository.addRecord("recordValue")
        tag = repository.addTag("tagValue")
    }
    @Test
    fun testTagRecordSuccess() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            post("/records/${record.id}/tags/${tag.id}")
        } then {
            statusCode(201)
            body("id", CoreMatchers.equalTo(record.id))
            body("record", CoreMatchers.equalTo(record.record))
            body("tags", hasSize<Array<Tag>>(1))
            body("tags[0].id", CoreMatchers.equalTo(tag.id))
            body("tags[0].tag", CoreMatchers.equalTo(tag.tag))
        }
    }

    @Test
    fun testTagRecordInvalidRecordIdNotFound() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            post("/records/${Int.MAX_VALUE}/tags/${tag.id}")
        } then {
            statusCode(404)
        }
    }

    @Test
    fun testTagRecordInvalidTagIdNotFound() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            post("/records/${record.id}/tags/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }


    @Test
    fun testTagRecordInvalidRecordIdNaNBadRequest() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            post("/records/recordId/tags/${tag.id}")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testTagRecordInvalidTagIdNaNBadRequest() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            post("/records/${record.id}/tags/tagId")
        } then {
            statusCode(400)
        }
    }
}
