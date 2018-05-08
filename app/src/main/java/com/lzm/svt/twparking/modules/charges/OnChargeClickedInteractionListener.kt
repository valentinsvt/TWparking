package com.lzm.svt.twparking.modules.charges

import com.lzm.svt.twparking.modules.charges.charge.ChargeItem

interface OnChargeClickedInteractionListener {
    fun onChargePressed(item: ChargeItem?)
}