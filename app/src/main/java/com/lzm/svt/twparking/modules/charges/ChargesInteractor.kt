package com.lzm.svt.twparking.modules.charges

import com.lzm.svt.twparking.BFFClient
import com.lzm.svt.twparking.Urls
import java.util.*

class ChargesInteractor : ChargesContracts.ChargesInteractorType {
    override fun loadMonthAndYearSpinners(delegate: ChargesContracts.GenerateChargesDelegate) {
        val months = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
                "Octubre", "Noviembre", "Diciembre")

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

    override fun createCharges(delegate: ChargesContracts.GenerateChargesDelegate, month: String, year: String, token: String, client: BFFClient) {
        val path = "${Urls.CHARGES.value}/createForMonth"
        val params = HashMap<String, String>()
        params["month"] = month
        params["year"] = year
        client.makePostRequest(path, params, token, delegate)
    }

}