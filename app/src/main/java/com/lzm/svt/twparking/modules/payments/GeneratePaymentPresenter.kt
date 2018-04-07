package com.lzm.svt.twparking.modules.payments

class GeneratePaymentPresenter: Contracts.PaymentsPresenterType, Contracts.GeneratePaymentsDelegate {
    override var view: Contracts.PaymentsViewType? = null
    override var interactor: Contracts.PaymentsInteractorType? = null

    override fun onGeneratePaymentsViewCreated() {
        interactor?.loadMonthAndYearSpinners(this)
    }

    override fun spinnersLoaded(months: Array<String>, years: Array<Int>) {
        view?.populateSpinners(months, years)
    }

    override fun generateButtonPressed(month: String, year: String) {
    }
}