package com.msagi.recordtagger.restapi

import com.msagi.recordtagger.Server
import com.msagi.recordtagger.TestConfigProvider
import com.msagi.recordtagger.repository.RepositoryTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.junit.AfterClass
import org.junit.BeforeClass
import java.nio.charset.Charset

open class RestApiTest : RepositoryTest() {

    companion object {

        private lateinit var server: Server

        var contentTypeUrlEnc: String = ContentType.URLENC.withCharset(Charset.forName("UTF-8"))

        @BeforeClass  @JvmStatic fun beforeClass() {
            server = TestConfigProvider.provideTestServer()
            Thread {
                server.start(wait = true)
            }.start()
        }

        @AfterClass @JvmStatic fun afterClass() {
            server.stop()
        }
    }

    private lateinit var request: RequestSpecification
    private lateinit var response: Response

    fun given(f: RequestSpecification.() -> RequestSpecification): RestApiTest = apply {
        request = RestAssured.given().spec(TestConfigProvider.provideTestRestApi()).f()
    }

    fun RequestSpecification.emptyBody(): RequestSpecification = this

    infix fun `when`(f: RequestSpecification.() -> Response): RestApiTest = apply {
       response = request.`when`().f()
    }

    infix fun then(f: ValidatableResponse.() -> ValidatableResponse): RestApiTest = apply {
        response.then().f()
    }
}