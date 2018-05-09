package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.modules.payments.payment.PaymentItem

interface OnPaymentClickedInteractionListener {
    fun onPaymentPressed(item: PaymentItem?)
}