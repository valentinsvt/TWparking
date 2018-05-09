package com.lzm.svt.twparking

import java.text.NumberFormat

class Utils {
    companion object {
        fun getMonths(): Array<String> {
            return arrayOf("ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE",
                    "OCTUBRE", "NOVIEMBRE", "DICIEMBRE")
        }

        fun formatNumber(number: Double):String {
            val nf = NumberFormat.getInstance()
            return nf.format(number)
        }
    }
}