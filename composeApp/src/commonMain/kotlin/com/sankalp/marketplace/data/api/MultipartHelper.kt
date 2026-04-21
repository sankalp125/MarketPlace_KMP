package com.sankalp.marketplace.data.api

import com.sankalp.marketplace.data.models.MultipartRequest
import com.sankalp.marketplace.utils.readFileBytes
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders


object MultipartHelper {
    fun build(request : MultipartRequest) : MultiPartFormDataContent{
        return  MultiPartFormDataContent(
            formData {

                // 🔹 Form Fields
                request.formFields.forEach { (key, value) ->
                    append(key, value)
                }

                // 🔹 Files
                request.files.forEach { file ->

                    val bytes = readFileBytes(file.filePath)

                    append(
                        key = file.key,
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, file.mimeType)
                            append(
                                HttpHeaders.ContentDisposition,
                                "form-data; name=\"${file.key}\"; filename=\"${file.fileName}\""
                            )
                        }
                    )
                }
            }
        )
    }
}