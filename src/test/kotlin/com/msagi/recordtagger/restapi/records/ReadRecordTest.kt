package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Test

class ReadRecordTest : RestApiTest() {

    val tags = mutableListOf<Tag>()
    lateinit var record: Record

    @Before
    override fun before() {
        super.before()

        //create a record tagged by 10 tags
        val recordValue = "recordValue"
        record = repository.addRecord(recordValue)
        for (i in 0 until 10) {
            val tag = repository.addTag("tagValue$i")
            repository.tag(record.id, tag.id)
            tags.add(tag)
        }
    }

    @Test
    fun testReadRecordSuccess() {
        given {
            contentType(ContentType.JSON)
        } `when` {
            get("/records/${record.id}")
        } then {
            body("id", equalTo(record.id))
            body("record", equalTo(record.record))
            body("tags.size", equalTo(tags.size))
            for (i in 0 until 10) {
                body("tags[$i].id", equalTo(tags[i].id))
                body("tags[$i].tag", equalTo(tags[i].tag))
            }
            statusCode(200)
        }
    }

    @Test
    fun testReadNonExistingRecordNotFound() {
        given {
            contentType(ContentType.JSON)
        } `when` {
            get("/records/${Int.MAX_VALUE}")
        } then {
            statusCode(404)
        }
    }

    @Test
    fun testReadNaNInvalidRecordBadRequest() {
        given {
            contentType(ContentType.JSON)
        } `when` {
            get("/records/recordId")
        } then {
            statusCode(400)
        }
    }

}