package com.lzm.svt.twparking.contracts

import android.content.Context
import android.support.v4.app.Fragment

class WireframeContracts {
    interface AppWireframe {
        fun open(itemId: Int): Fragment?
    }
    interface WireframeDelegate {
        fun showFragment(fragment: Fragment)
    }
}