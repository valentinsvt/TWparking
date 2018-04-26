package com.lzm.svt.twparking.modules.charges

import org.json.JSONObject

class ChargePresenter: ChargesContracts.ChargesPresenterType, ChargesContracts.GenerateChargesDelegate {
    override fun success(response: JSONObject) {
        println("---->  " + response.get("result"))
    }

    override fun error() {
        println("---->  Error")

    }

    override var view: ChargesContracts.ChargesViewType? = null
    override var interactor: ChargesContracts.ChargesInteractorType? = null
    override var router: ChargesWireframe? = null

    override fun onGenerateChargesViewCreated() {
        interactor?.loadMonthAndYearSpinners(this)
    }

    override fun spinnersLoaded(months: Array<String>, years: Array<Int>) {
        view?.populateSpinners(months, years)
    }

    override fun generateButtonPressed(month: String, year: String) {
        router?.let {
            interactor?.createCharges(this, month, year, it.mainWireframe.bffClient)
        }
    }
}