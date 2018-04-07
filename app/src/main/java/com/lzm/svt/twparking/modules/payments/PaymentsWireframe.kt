package com.lzm.svt.twparking.modules.payments

import android.support.v4.app.Fragment
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.contracts.WireframeContracts

class PaymentsWireframe : WireframeContracts.AppWireframe {
    override fun open(itemId: Int): Fragment? {
        val context = this
        when (itemId) {
            R.id.drawer_payments_option -> {
                val generatePaymentFragment = GeneratePaymentView()
                val interactor = PaymentInteractor()
                val presenter = GeneratePaymentPresenter()
                presenter.interactor = interactor
                presenter.view = generatePaymentFragment
                generatePaymentFragment.presenter = presenter
                generatePaymentFragment.router = context
                return generatePaymentFragment
            }
        }
        return null
    }

}