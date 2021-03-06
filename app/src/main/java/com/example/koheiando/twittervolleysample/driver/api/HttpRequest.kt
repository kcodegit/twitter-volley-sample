package com.example.koheiando.twittervolleysample.driver.api

import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.example.koheiando.twittervolleysample.TvsApplication
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass

abstract class HttpRequest {
    abstract val url: String
    abstract val method: Int
    open val retryCount: Int = 1

    companion object {
        private val TAG = this::class.java.simpleName
    }

    protected suspend fun <T : HttpResponse> request(
            responseClass: KClass<T>,
            headers: Map<String, String>,
            queryParams: Map<String, String>,
            jsonRequest: JSONObject?
    ): T {
        return suspendCancellableCoroutine { continuation ->
            val success = Response.Listener<JSONObject> { response ->
                continuation.resume(
                        responseClass.java.newInstance().parseJson(response) as T
                )
            }

            val error = Response.ErrorListener { error -> continuation.resumeWithException(error) }

            val request = object : JsonObjectRequest(method, queryParams.toUrl(), jsonRequest, success, error) {
                override fun getHeaders(): Map<String, String> = headers

                override fun parseNetworkError(volleyError: VolleyError): VolleyError {
                    return volleyError.networkResponse?.let {
                        Log.e(TAG, volleyError.networkResponse.headers.toString())
                        Log.e(TAG, String(volleyError.networkResponse.data))
                        VolleyError(String(volleyError.networkResponse.data))
                    } ?: volleyError
                }
            }

            request.retryPolicy = HttpRetryPolicy(maxRetry = retryCount)
            continuation.invokeOnCancellation { request.cancel() }

            try {
                HttpRequestQueue.getInstance(TvsApplication.getAppContext()).addToRequestQueue(request)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    protected suspend fun <T : HttpResponse> request(
            responseClass: KClass<T>,
            headers: Map<String, String>,
            queryParams: Map<String, String>,
            body: String
    ): T {
        return suspendCancellableCoroutine { continuation ->
            val success = Response.Listener<JSONObject> { response ->
                continuation.resume(responseClass.java.newInstance().parseJson(response) as T)
            }

            val error = Response.ErrorListener { error -> continuation.resumeWithException(error) }

            val request = object : JsonObjectRequest(method, queryParams.toUrl(), null, success, error) {
                override fun getHeaders(): Map<String, String> = headers

                override fun getBody(): ByteArray {
                    return body.toByteArray()
                }

                override fun parseNetworkError(volleyError: VolleyError): VolleyError {
                    return volleyError.networkResponse?.let {
                        Log.e(TAG, volleyError.networkResponse.headers.toString())
                        Log.e(TAG, String(volleyError.networkResponse.data))
                        VolleyError(String(volleyError.networkResponse.data))
                    } ?: volleyError
                }
            }

            request.retryPolicy = HttpRetryPolicy(maxRetry = retryCount)
            continuation.invokeOnCancellation { request.cancel() }

            try {
                HttpRequestQueue.getInstance(TvsApplication.getAppContext()).addToRequestQueue(request)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun Map<String, String>.toUrl(): String {
        return url + if (this.isNotEmpty()) this.keys.fold("?") { pre, cur -> "$pre$cur=${this[cur]!!}" } else ""
    }
}


