package com.lzm.svt.twparking.modules.payments

import org.json.JSONObject

class PaymentsPresenter : PaymentsContracts.PaymentsPresenterType, PaymentsContracts.GeneratePaymentsDelegate {
    override fun success(response: JSONObject) {
//        println("---->  " + response.get("result"))
    }

    override fun error() {
        println("---->  Error")

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