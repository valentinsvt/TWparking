package com.lzm.svt.twparking.contracts

import com.android.volley.VolleyError
import org.json.JSONObject

class HttpContracts {
    interface HttpDelegate {
        fun success(response: JSONObject)
        fun error(error: VolleyError)
    }
}