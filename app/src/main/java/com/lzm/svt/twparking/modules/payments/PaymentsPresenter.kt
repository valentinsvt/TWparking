package com.lzm.svt.twparking.modules.payments

import com.android.volley.VolleyError
import org.json.JSONArray

class PaymentsPresenter : PaymentsContracts.PaymentsPresenterType, PaymentsContracts.GeneratePaymentsDelegate {
    override fun success(response: JSONArray) {
        println("----> Success:  $response")
    }

    override fun error(error: VolleyError) {
        println("---->  Error ${error.message}")

    }

    override var view: PaymentsContracts.PaymentsViewType? = null
    override var interactor: PaymentsContracts.PaymentsInteractorType? = null
    override var router: PaymentsWireframe? = null

    override fun onGeneratePaymentsViewCreated() {
        interactor?.loadMonthAndYearSpinners(this)
    }

    override fun spinnersLoaded(months: Array<String>, years: Array<Int>) {
        view?.populateSpinners(months, years)
    }

    override fun generateButtonPressed(month: String, year: String, token: String) {
        router?.let {
            interactor?.createPayments(this, month, year, token, it.mainWireframe.bffClient)
        }
    }
}