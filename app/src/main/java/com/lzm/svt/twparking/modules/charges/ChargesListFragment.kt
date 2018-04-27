package com.lzm.svt.twparking.modules.charges

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.modules.charges.dummy.DummyContent
import com.lzm.svt.twparking.modules.charges.dummy.DummyContent.DummyItem
import java.util.*

class ChargesListFragment : Fragment() {

    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_charge_list, container, false)

        val chargesList = view.findViewById(R.id.charges_list) as RecyclerView
        with(chargesList) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }
            adapter = MyChargeRecyclerViewAdapter(DummyContent.ITEMS, listener)
        }

        val months = arrayOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
                "Octubre", "Noviembre", "Diciembre")

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)

        populateSpinners(view, months, years)

        return view
    }


    private fun populateSpinners(view: View, months: Array<String>, years: Array<Int>) {

        val spinnerMonths = view.findViewById(R.id.spinner_months) as Spinner
        val spinnerYears = view.findViewById(R.id.spinner_years) as Spinner

        spinnerMonths.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, months)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }

        spinnerYears.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, years)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onChargePressed(item: DummyItem?)
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                ChargesListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
