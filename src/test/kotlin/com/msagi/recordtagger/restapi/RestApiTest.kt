package com.msagi.recordtagger.restapi

import com.msagi.recordtagger.Server
import com.msagi.recordtagger.TestConfigProvider
import com.msagi.recordtagger.repository.RepositoryTest
import io.restassured.RestAssured
import io.restassured.config.HttpClientConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.HttpRequestRetryHandler
import org.junit.AfterClass
import org.junit.BeforeClass
import java.nio.charset.Charset

open class RestApiTest : RepositoryTest() {

    companion object {

        private lateinit var server: Server

        val contentTypeUrlEnc: String = ContentType.URLENC.withCharset(Charset.forName("UTF-8"))

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            server = TestConfigProvider.provideTestServer()
            Thread {
                server.start()
            }.start()
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            server.stop()
        }

        /**
         * Retry request 3x before setting the test to fail
         */
        fun getRestAssuredConfig(): RestAssuredConfig {
            val htc = HttpClientConfig.HttpClientFactory {
                val client = DefaultHttpClient()
                client.httpRequestRetryHandler = HttpRequestRetryHandler()
                client
            }
            return RestAssured.config().httpClient(HttpClientConfig.httpClientConfig().httpClientFactory(htc))
        }
    }

    private lateinit var request: RequestSpecification
    private lateinit var response: Response

    fun given(f: RequestSpecification.() -> RequestSpecification): RestApiTest = apply {
        request = RestAssured
                .given()
                .config(Companion.getRestAssuredConfig())
                .spec(TestConfigProvider.provideTestRestApi())
                .f()
    }

    fun RequestSpecification.emptyBody(): RequestSpecification = this

    infix fun `when`(f: RequestSpecification.() -> Response): RestApiTest = apply {
        response = request.`when`().f()
    }

    infix fun then(f: ValidatableResponse.() -> ValidatableResponse): RestApiTest = apply {
        response.then().f()
    }
}