package com.lzm.svt.twparking.modules.charges

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.lzm.svt.twparking.NetworkQueue
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.Urls
import com.lzm.svt.twparking.modules.charges.charge.ChargeItem
import kotlinx.android.synthetic.main.fragment_charge_list.*
import java.util.*

class ChargesListFragment : Fragment() {

    private var columnCount = 1

    private var listener: OnChargeClickedInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_charge_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val months = arrayOf("ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE",
                "OCTUBRE", "NOVIEMBRE", "DICIEMBRE")

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)

        val context = this.context

        populateSpinners(months, years)

        show_charges_button.setOnClickListener {
            with(charges_list) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyChargeRecyclerViewAdapter(context!!, ArrayList(), listener)
            }
            val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")

                val month = spinner_months.selectedItem.toString()
                val year = spinner_years.selectedItem.toString()

                val networkQueue = NetworkQueue.getInstance(context)
                val url = Urls.BASE.value
                val path = "${Urls.CHARGES}?filter={\"where\":{\"month\":\"$month\",\"year\":\"$year\"}, \"include\":[\"person\"]}"

                val getRequest = object : JsonArrayRequest(Method.GET, url + path, null,
                        Response.Listener { response ->
                            val items: MutableList<ChargeItem> = ArrayList()

                            for (i in 0..(response.length() - 1)) {
                                val charge = response.getJSONObject(i)
                                val id = charge.getInt("id")
                                val amountPerson = charge.getDouble("amountPerson")
                                val amountPayed = charge.getDouble("amountPayed")
                                val date = charge.getString("date")
                                val person = charge.getJSONObject("person")
                                val name = person.getString("name")
                                val preferredPayment = person.getString("preferredPaymentMethod")

                                val item = ChargeItem(id.toString(),
                                        amountPerson, amountPayed, date, name, preferredPayment)
                                items.add(item)
                            }

                            val sortedList = items.sortedWith(compareBy({ it.amountPayed }, { it.name }))

                            with(charges_list) {
                                layoutManager = when {
                                    columnCount <= 1 -> LinearLayoutManager(context)
                                    else -> GridLayoutManager(context, columnCount)
                                }
                                adapter = MyChargeRecyclerViewAdapter(context, sortedList, listener)
                            }
                        },
                        Response.ErrorListener {
                            println("----------------------------------------------------------------")
                            println("ERROR!!!")
                            println(it)
                            println("----------------------------------------------------------------")
                        }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["Accept"] = "application/json"
                        headers["Authorization"] = token
                        return headers
                    }
                }
                networkQueue.addToRequestQueue(getRequest)
            }
        }
    }


    private fun populateSpinners(months: Array<String>, years: Array<Int>) {
        spinner_months.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, months)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }

        spinner_years.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, years)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnChargeClickedInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnChargeClickedInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnChargeClickedInteractionListener {
        fun onChargePressed(item: ChargeItem?)
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
