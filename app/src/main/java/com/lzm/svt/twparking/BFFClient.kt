package com.lzm.svt.twparking

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.lzm.svt.twparking.contracts.HttpContracts
import org.json.JSONObject
import java.util.*

class BFFClient(private val networkQueue: NetworkQueue) {
    val URL = Urls.BASE.value

    fun makePostRequest(path: String, params: HashMap<String, String>, token: String, delegate: HttpContracts.HttpDelegate) {
        val postRequest = object : JsonObjectRequest(Request.Method.POST, URL + path, null,
                Response.Listener { response ->
                    delegate.success(response)
                },
                Response.ErrorListener { error ->
                    delegate.error(error)
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                headers["Authorization"] = token
                return headers
            }

            override fun getBody(): ByteArray {
                return JSONObject(params).toString().toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        networkQueue.addToRequestQueue(postRequest)
    }
}
