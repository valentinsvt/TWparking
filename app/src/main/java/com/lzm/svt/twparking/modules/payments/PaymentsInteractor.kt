package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.BFFClient
import com.lzm.svt.twparking.Months
import com.lzm.svt.twparking.Urls
import java.util.*

class PaymentsInteractor : PaymentsContracts.PaymentsInteractorType {
    override fun loadMonthAndYearSpinners(delegate: PaymentsContracts.GeneratePaymentsDelegate) {
        val months = Months.getAll()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

    override fun createPayments(delegate: PaymentsContracts.GeneratePaymentsDelegate, month: String, year: String, token: String, client: BFFClient) {
        val path = "${Urls.PAYMENTS.value}/createForMonth"
        val params = HashMap<String, String>()
        params["month"] = month
        params["year"] = year
        client.makePostRequest(path, params, token, delegate)
    }

}