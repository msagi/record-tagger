package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

class UntagRecordTest : RestApiTest() {

    private lateinit var record: Record
    private lateinit var tag: Tag

    @Before
    override fun before() {
        super.before()

        record = repository.addRecord("recordValue")
        tag = repository.addTag("tagValue")
    }

    @Test
    fun testUntagRecordSuccess() {
        repository.tag(record.id, tag.id)
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            delete("/records/${record.id}/tags/${tag.id}")
        } then {
            statusCode(200)
            body("id", CoreMatchers.equalTo(record.id))
            body("record", CoreMatchers.equalTo(record.record))
            body("tags", Matchers.hasSize<Array<Tag>>(0))
        }
    }

    @Test
    fun testUntagRecordInvalidRecordIdNotFound() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            delete("/records/${Int.MAX_VALUE}/tags/${tag.id}")
        } then {
            statusCode(404)
        }
    }

    @Test
    fun testUntagRecordInvalidTagIdNotFound() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            delete("/records/${record.id}/tags/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

    @Test
    fun testUntagRecordInvalidRecordIdNaNBadRequest() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            delete("/records/recordId/tags/${tag.id}")
        } then {
            statusCode(400)
        }
    }

    @Test
    fun testUntagRecordInvalidTagIdNaNBadRequest() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            delete("/records/${record.id}/tags/tagId")
        } then {
            statusCode(400)
        }
    }

}
