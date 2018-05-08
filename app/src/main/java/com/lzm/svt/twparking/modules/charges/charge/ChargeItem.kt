package com.lzm.svt.twparking.modules.charges.charge

data class ChargeItem(val id: String,
                      val amountPerson: Double,
                      var amountPayed: Double,
                      var date: String,
                      val name: String,
                      val preferredPaymentMethod: String) {
}