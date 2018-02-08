package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.repository.Record
import com.msagi.recordtagger.repository.Tag
import com.msagi.recordtagger.restapi.RestApiTest
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.After
import org.junit.Before
import org.junit.Test

class ReadRecordsTest : RestApiTest() {

    private val tags = mutableListOf<Tag>()
    private val records = mutableListOf<Record>()

    @Before
    override fun before() {
        super.before()

        //create 2 tags, 10 records and tag even records with the first, odd records with the second tag
        for (i in 0 until 2) {
            tags.add(repository.addTag("tagValue$i"))
        }
        for (i in 0 until 10) {
            val record = repository.addRecord("recordValue$i")
            records.add(record)
            when(i % 2) {
                0 -> repository.tag(record.id, tags[0].id)
                1 -> repository.tag(record.id, tags[1].id)
            }
        }
    }

    @Test
    fun testReadRecordsNoSelectedSuccess() {
        given {
            emptyBody()
            contentType(ContentType.JSON)
        } `when` {
            get("/records")
        } then {
            body("total", equalTo(10))
            statusCode(200)
        }
    }

    @Test
    fun testReadRecordsSelected1Success() {
        given {
            param("tags","tagValue0")
            contentType(ContentType.JSON)
        } `when` {
            get("/records")
        } then {
            body("total", equalTo(5))
            for (i in 0 until 5) {
                body("_embedded.records[$i].tags[0].id", equalTo(tags[0].id))
                body("_embedded.records[$i].tags[0].tag", equalTo(tags[0].tag))
            }
            statusCode(200)
        }
    }

    @Test
    fun testReadRecordsSelected2Success() {
        given {
            param("tags","tagValue1")
            contentType(ContentType.JSON)
        } `when` {
            get("/records")
        } then {
            body("total", equalTo(5))
            body("_embedded.records", hasSize<Array<Record>>(5))
            for (i in 0 until 5) {
                body("_embedded.records[$i].tags[0].id", equalTo(tags[1].id))
                body("_embedded.records[$i].tags[0].tag", equalTo(tags[1].tag))
            }
            statusCode(200)
        }
    }

    @Test
    fun testReadRecordsLimitSuccess() {
        given {
            param("tags","tagValue1")
            param("limit","3")
            contentType(ContentType.JSON)
        } `when` {
            get("/records")
        } then {
            body("total", equalTo(5))
            body("_embedded.records", hasSize<Array<Record>>(3))
            statusCode(200)
        }
    }

    @Test
    fun testReadRecordsOffsetSuccess() {
        given {
            param("tags","tagValue1")
            param("offset","1")
            contentType(ContentType.JSON)
        } `when` {
            get("/records")
        } then {
            body("total", equalTo(5))
            body("_embedded.records", hasSize<Array<Record>>(4))
            statusCode(200)
        }
    }

}