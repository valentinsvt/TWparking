package com.lzm.svt.twparking

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.lzm.svt.twparking.contracts.HttpContracts
import org.json.JSONObject
import java.util.HashMap

class BFFClient(val networkQueue: NetworkQueue) {
    val URL = "http://192.168.100.39:3000/api/"

    fun makePostRequest(path: String, params: HashMap<String, String>, delegate: HttpContracts.HttpDelegate) {
        val postRequest = object : JsonObjectRequest(Request.Method.POST, URL + path, null,
                Response.Listener { response ->
                    println("Response is: ${response.get("result")}")
                    delegate.success(response)
                },
                Response.ErrorListener {
                    delegate.error()
                }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
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