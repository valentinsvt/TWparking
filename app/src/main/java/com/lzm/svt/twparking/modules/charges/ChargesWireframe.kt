package com.lzm.svt.twparking.modules.charges

import android.support.v4.app.Fragment
import com.lzm.svt.twparking.MainWireframe
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.contracts.WireframeContracts

class ChargesWireframe(override var mainWireframe: MainWireframe) : WireframeContracts.AppWireframe {

    override fun open(itemId: Int): Fragment? {
        val router = this
        when (itemId) {
            R.id.drawer_charges_generate_option -> {
                val generateChargesFragment = GenerateChargeView()
                val interactor = ChargeInteractor()
                val presenter = ChargePresenter()
                presenter.interactor = interactor
                presenter.view = generateChargesFragment
                presenter.router = router
                generateChargesFragment.presenter = presenter
                return generateChargesFragment
            }
        }
        return null
    }

}