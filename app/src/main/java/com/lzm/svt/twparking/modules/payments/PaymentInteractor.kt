package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.BFFClient
import java.util.*


class PaymentInteractor: Contracts.PaymentsInteractorType {
    override fun loadMonthAndYearSpinners(delegate: Contracts.GeneratePaymentsDelegate) {
        val months = arrayOf("Enero","Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years  = arrayOf(currentYear-1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

    override fun createPayments(delegate: Contracts.GeneratePaymentsDelegate, month: String, year: String, client: BFFClient) {
        val path = "payments/createForMonth"
        val params = HashMap<String, String>()
        params["month"] = month
        params["year"] = year
        client.makePostRequest(path, params, delegate)
    }

}