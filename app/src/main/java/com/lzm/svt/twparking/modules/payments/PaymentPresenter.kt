package com.lzm.svt.twparking.modules.payments

import org.json.JSONObject

class PaymentPresenter: Contracts.PaymentsPresenterType, Contracts.GeneratePaymentsDelegate {
    override fun success(response: JSONObject) {
        println("---->  " + response.get("result"))
    }

    override fun error() {
        println("---->  Error")

    }

    override var view: Contracts.PaymentsViewType? = null
    override var interactor: Contracts.PaymentsInteractorType? = null
    override var router: PaymentsWireframe? = null

    override fun onGeneratePaymentsViewCreated() {
        interactor?.loadMonthAndYearSpinners(this)
    }

    override fun spinnersLoaded(months: Array<String>, years: Array<Int>) {
        view?.populateSpinners(months, years)
    }

    override fun generateButtonPressed(month: String, year: String) {
        router?.let {
            interactor?.createPayments(this, month, year, it.mainWireframe.bffClient)
        }
    }
}