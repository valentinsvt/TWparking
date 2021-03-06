package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.BFFClient
import com.lzm.svt.twparking.Urls
import com.lzm.svt.twparking.Utils
import java.util.*

class PaymentsInteractor : PaymentsContracts.PaymentsInteractorType {
    override fun loadMonthAndYearSpinners(delegate: PaymentsContracts.GeneratePaymentsDelegate) {
        val months = Utils.getMonths()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

    override fun createPayments(delegate: PaymentsContracts.GeneratePaymentsDelegate, month: String, year: String, token: String, client: BFFClient) {
        val path = "${Urls.PAYMENTS.value}/${Urls.CREATE_FOR_MONTH.value}"
        val params = HashMap<String, String>()
        params["month"] = month
        params["year"] = year
        client.makePostRequest(path, params, token, delegate)
    }

}