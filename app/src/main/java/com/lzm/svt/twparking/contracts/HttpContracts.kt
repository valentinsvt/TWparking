package com.lzm.svt.twparking.contracts

import org.json.JSONObject

class HttpContracts {
    interface HttpDelegate {
        fun success(response: JSONObject)
        fun error()
    }
}