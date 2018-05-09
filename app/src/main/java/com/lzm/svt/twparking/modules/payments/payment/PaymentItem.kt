package com.lzm.svt.twparking.modules.payments.payment

data class PaymentItem(val id: String,
                      val amount: Double,
                      var amountPayed: Double,
                      var date: String,
                      val name: String) {
}