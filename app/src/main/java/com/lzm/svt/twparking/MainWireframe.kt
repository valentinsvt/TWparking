package com.lzm.svt.twparking

import android.content.Context
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.lzm.svt.twparking.contracts.WireframeContracts
import com.lzm.svt.twparking.modules.charges.ChargesWireframe
import com.lzm.svt.twparking.modules.payments.PaymentsWireframe

class MainWireframe(var bffClient: BFFClient) {
    var routers = arrayListOf<WireframeContracts.AppWireframe>()

    fun open(delegate: WireframeContracts.WireframeDelegate, selectedOption: MenuItem) {
        routers.forEach {
            val fragment: Fragment? = it.open(selectedOption.itemId)
            if (fragment != null) {
                delegate.showFragment(fragment)
                return
            }
        }
    }

    companion object {
        @JvmStatic
        private var instance: MainWireframe? = null

        fun getCurrentInstance(context: Context): MainWireframe {
            val currentInstance = this.instance?.let { it }
                    ?: MainWireframe(BFFClient(NetworkQueue.getInstance(context))).apply {
                        routers.add(PaymentsWireframe(this))
                        routers.add(ChargesWireframe(this))
                    }
            this.instance = currentInstance
            return currentInstance
        }
    }
}