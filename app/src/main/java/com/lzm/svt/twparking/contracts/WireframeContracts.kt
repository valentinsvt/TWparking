package com.lzm.svt.twparking.contracts

import android.support.v4.app.Fragment
import com.lzm.svt.twparking.MainWireframe

class WireframeContracts {
    interface AppWireframe {
        var mainWireframe: MainWireframe
        fun open(itemId: Int): Fragment?
    }
    interface WireframeDelegate {
        fun showFragment(fragment: Fragment)
    }
}