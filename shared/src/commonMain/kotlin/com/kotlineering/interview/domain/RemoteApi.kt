package com.kotlineering.interview.domain

import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom

// Provide methods to centralize configuration of remote calls
// (eg: setting base domain, configuring Content-Type, etc...)
abstract class RemoteApi(
    val domain: String
) {
    protected suspend fun HttpClient.get(path: String, list: Map<String, String> = emptyMap()) =
        get {
            accept(ContentType.Application.Json)
            url {
                list.takeIf { it.isNotEmpty() }?.forEach {
                    parameters.append(it.key, it.value)
                }
                takeFrom(domain)
                encodedPath = path
            }
        }

    protected suspend fun HttpClient.delete(path: String) =
        delete {
            accept(ContentType.Application.Json)
            url {
                takeFrom(domain)
                encodedPath = path
            }
        }

    protected suspend inline fun <reified T> HttpClient.post(path: String, data: T): HttpResponse =
        post {
            accept(ContentType.Application.Json)
            url {
                takeFrom(domain)
                encodedPath = path
            }
            contentType(ContentType.Application.Json)
            setBody(data)
        }

    protected suspend inline fun <reified T> HttpClient.put(path: String, data: T): HttpResponse =
        put {
            accept(ContentType.Application.Json)
            url {
                takeFrom(domain)
                encodedPath = path
            }
            contentType(ContentType.Application.Json)
            setBody(data)
        }
}
