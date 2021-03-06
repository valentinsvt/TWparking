package com.lzm.svt.twparking.modules.payments

import android.support.v4.app.Fragment
import com.lzm.svt.twparking.MainWireframe
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.contracts.WireframeContracts

class PaymentsWireframe(override var mainWireframe: MainWireframe) : WireframeContracts.AppWireframe {

    override fun open(itemId: Int): Fragment? {
        val router = this
        when (itemId) {
            R.id.drawer_payments_generate_option -> {
                val generatePaymentFragment = GeneratePaymentsView()
                val interactor = PaymentsInteractor()
                val presenter = PaymentsPresenter()
                presenter.interactor = interactor
                presenter.view = generatePaymentFragment
                presenter.router = router
                generatePaymentFragment.presenter = presenter
                return generatePaymentFragment
            }
            R.id.drawer_payments_list_option -> {
                return PaymentsListFragment.newInstance(1)
            }
        }
        return null
    }

}