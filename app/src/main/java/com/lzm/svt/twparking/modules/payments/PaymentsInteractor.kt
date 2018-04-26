package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.BFFClient
import java.util.*

class PaymentsInteractor : PaymentsContracts.PaymentsInteractorType {
    override fun loadMonthAndYearSpinners(delegate: PaymentsContracts.GeneratePaymentsDelegate) {
        val months = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
                "Octubre", "Noviembre", "Diciembre")

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

    override fun createPayments(delegate: PaymentsContracts.GeneratePaymentsDelegate, month: String, year: String, client: BFFClient) {
        val path = "payments/createForMonth"
        val params = HashMap<String, String>()
        params["month"] = month
        params["year"] = year
        client.makePostRequest(path, params, delegate)
    }

}