package com.lzm.svt.twparking.contracts

import com.android.volley.VolleyError
import org.json.JSONArray

class HttpContracts {
    interface HttpDelegate {
        fun success(response: JSONArray)
        fun error(error: VolleyError)
    }
}