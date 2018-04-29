package com.lzm.svt.twparking.modules.payments

import com.lzm.svt.twparking.BFFClient
import com.lzm.svt.twparking.contracts.HttpContracts

class PaymentsContracts {
    interface PaymentsPresenterType {
        var interactor: PaymentsInteractorType?
        var view: PaymentsViewType?
        var router: PaymentsWireframe?
        fun onGeneratePaymentsViewCreated()
        fun generateButtonPressed(month: String, year: String, token: String)
    }

    interface PaymentsInteractorType {
        fun loadMonthAndYearSpinners(delegate: GeneratePaymentsDelegate)
        fun createPayments(delegate: GeneratePaymentsDelegate, month: String, year: String, token: String, client: BFFClient)
    }

    interface PaymentsViewType {
        fun populateSpinners(months: Array<String>, years: Array<Int>)
    }

    interface GeneratePaymentsDelegate : HttpContracts.HttpDelegate {
        fun spinnersLoaded(months: Array<String>, years: Array<Int>)
    }
}