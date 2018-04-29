package com.lzm.svt.twparking.modules.charges

import com.lzm.svt.twparking.BFFClient
import com.lzm.svt.twparking.contracts.HttpContracts

class ChargesContracts {
    interface ChargesPresenterType {
        var interactor: ChargesInteractorType?
        var view: ChargesViewType?
        var router: ChargesWireframe?
        fun onGenerateChargesViewCreated()
        fun generateButtonPressed(month: String, year: String, token: String)
    }

    interface ChargesInteractorType {
        fun loadMonthAndYearSpinners(delegate: GenerateChargesDelegate)
        fun createCharges(delegate: GenerateChargesDelegate, month: String, year: String, token: String, client: BFFClient)
    }

    interface ChargesViewType {
        fun populateSpinners(months: Array<String>, years: Array<Int>)
    }

    interface GenerateChargesDelegate : HttpContracts.HttpDelegate {
        fun spinnersLoaded(months: Array<String>, years: Array<Int>)
    }
}