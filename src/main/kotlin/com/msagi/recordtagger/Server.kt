/*
 * Copyright 2018 Miklos Sagi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msagi.recordtagger

import com.github.salomonbrys.kodein.*
import com.msagi.recordtagger.repository.Repository
import com.msagi.recordtagger.repository.sql.SqlRepository
import com.msagi.recordtagger.restapi.records.RecordsRestApi
import com.msagi.recordtagger.restapi.tags.TagsRestApi
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.resource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.util.concurrent.TimeUnit

interface RequestHandler {
    fun getHandler(): Routing.() -> Unit
}

class Server constructor (di: Kodein? = null) : KodeinAware {

    override val kodein = di ?: Kodein {

        constant("debug") with false

        bind<Repository>() with singleton {
            SqlRepository(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://127.0.0.1/recordtagger",
                    "recota",
                    "recota"
            )
        }

        bind<Logger>() with multiton { cls: Class<*> -> LoggerFactory.getLogger(cls) }

        constant("recordsPageSize") with 30

        constant("serverPort") with 8080
    }

    private val logger: Logger = kodein.withClassOf(this).instance()

    private lateinit var server: NettyApplicationEngine

    fun start(wait: Boolean = true) {
        logger.info("Initializing repository...")
        val debug: Boolean = kodein.instance("debug")

        val repository: Repository = kodein.instance()
        repository.initialize()

        val routingRules = arrayListOf(
                RecordsRestApi(kodein),
                TagsRestApi(kodein)
        )

        logger.info("Starting WebServer...")

        val serverPort: Int = kodein.instance("serverPort")
        server = embeddedServer(Netty, serverPort) {
            install(DefaultHeaders) {
                header(HttpHeaders.Server, "RecoTa")
            }
            install(Compression)
            if (debug) {
                install(CallLogging)
            }
            install(ContentNegotiation) {
                gson {
                    setDateFormat(DateFormat.LONG)
                    if (debug) {
                        setPrettyPrinting()
                    }
                    disableHtmlEscaping()
                }
            }
            routing {
                static("static") {
                    resources("css")
                    resources("js")
                    resource("index.html")
                    resource("tag-admin.html")
                }
                get("/") {
                    this.call.respondRedirect("/static/index.html")
                }
            }
            routingRules.forEach {
                routing(it.getHandler())
            }
        }

        server.start(wait)
    }

    fun stop() {
        server.stop(2, 2, TimeUnit.SECONDS)
    }
}

fun main(args: Array<String>) {
    Server().start()
}