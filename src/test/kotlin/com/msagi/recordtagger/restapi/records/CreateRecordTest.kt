package com.msagi.recordtagger.restapi.records

import com.msagi.recordtagger.restapi.RestApiTest
import org.hamcrest.CoreMatchers
import org.junit.Test

class CreateRecordTest : RestApiTest() {

    @Test
    fun testCreateRecordSuccess() {
        val recordValue = "recordValue"
        given {
            body("record=$recordValue")
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            post("/records")
        } then {
            body("record", CoreMatchers.equalTo(recordValue))
            statusCode(201)
        }
    }

    @Test
    fun testCreateRecordInvalidBadRequest() {
        given {
            emptyBody()
            contentType(Companion.contentTypeUrlEnc)
        } `when` {
            post("/records")
        } then {
            statusCode(400)
        }
    }

}
