package com.lzm.svt.twparking.modules.payments

import java.util.*

class PaymentInteractor: Contracts.PaymentsInteractorType {
    override fun loadMonthAndYearSpinners(delegate: Contracts.GeneratePaymentsDelegate) {
        val months = arrayOf("Enero","Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years  = arrayOf(currentYear-1, currentYear, currentYear + 1)
        delegate.spinnersLoaded(months, years)
    }

}