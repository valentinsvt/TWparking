package com.lzm.svt.twparking

enum class Urls(val value: String) {
    BASE("https://twparking-staging.herokuapp.com/api/"),
//    BASE("http://192.168.100.39:3000/api/"),
    CHARGES("charges"),
    PARAMS("params"),
    PAYMENTS("payments"),
    PEOPLE("people"),

    CREATE_FOR_MONTH("createForMonth"),
    LOGIN("login")
}