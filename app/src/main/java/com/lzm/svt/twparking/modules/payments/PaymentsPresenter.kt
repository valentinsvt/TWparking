package com.lzm.svt.twparking.modules.payments

import android.support.v4.app.Fragment
import com.android.volley.VolleyError
import com.lzm.svt.twparking.R
import org.json.JSONObject

class PaymentsPresenter : PaymentsContracts.PaymentsPresenterType, PaymentsContracts.GeneratePaymentsDelegate {
    override fun success(response: JSONObject) {
        val res = response.getJSONObject("result")
        val status = res.getInt("status")
        var message = ""
        println("----> Success:  $response   [$status]")

        when (status) {
            200 -> {
                message = res.getString("result")
            }
            201 -> {
                val result = res.getJSONArray("result")
                val total = result.length().toString()

                val fragment = view as Fragment
                val context = fragment.activity
                context?.let { message = it.getString(R.string.payments_create_for_month_success, total) }
            }
        }

//
        view?.showToast(message)
    }

    override fun error(error: VolleyError) {
        println("---->  Error ${error.message}")
        view?.showToast("Ha ocurrido un error.....")

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