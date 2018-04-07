package com.lzm.svt.twparking.modules.payments

class Contracts {
    interface PaymentsPresenterType {
        var interactor: PaymentsInteractorType?
        var view: PaymentsViewType?
        fun onGeneratePaymentsViewCreated()
        fun generateButtonPressed(month: String, year: String)
    }
    interface PaymentsInteractorType {
        fun loadMonthAndYearSpinners(delegate: GeneratePaymentsDelegate)
    }
    interface PaymentsViewType {
        fun populateSpinners(months: Array<String>, years: Array<Int>)
    }
    interface GeneratePaymentsDelegate {
        fun spinnersLoaded(months: Array<String>, years: Array<Int>)
    }
}