package com.lzm.svt.twparking.modules.charges

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.lzm.svt.twparking.NetworkQueue
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.Urls
import com.lzm.svt.twparking.modules.charges.charge.ChargeItem
import kotlinx.android.synthetic.main.fragment_charge_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ChargesListFragment : Fragment(), OnChargeClickedInteractionListener {

    private var myAdapter: MyChargeRecyclerViewAdapter? = null
    private var columnCount = 1
    private var payed = 0.0
    private var total = 0.0

    override fun onChargePressed(item: ChargeItem?) {
        val activity = this.context
        if (item != null && activity != null) {
            if (item.amountPayed == 0.0) {
                val sharedPref = activity.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                if (sharedPref != null) {
                    val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")
                    val networkQueue = NetworkQueue.getInstance(activity)
                    val path = "${Urls.CHARGES.value}/${item.id}"

                    val url = Urls.BASE.value

                    val dateFormat = "yyyy-MM-dd"
                    val sdf = SimpleDateFormat(dateFormat, Locale.US)
                    val today = Calendar.getInstance().time
                    val date = sdf.format(today)

                    val requestParams = HashMap<String, String>()
                    requestParams["amountPayed"] = "${item.amountPerson}"
                    requestParams["date"] = date

                    val postRequest = object : JsonObjectRequest(Method.PATCH, url + path, null,
                            Response.Listener {
                                Toast.makeText(activity, "Marcado ${item.name} como pagado", Toast.LENGTH_LONG).show()
                                item.amountPayed = item.amountPerson
                                payed += item.amountPayed
                                charges_total_payed.text = getPayedLabel(activity)
                                item.date = date
                                myAdapter?.sort()
                            },
                            Response.ErrorListener {
                                Toast.makeText(activity, "ERROR!!!", Toast.LENGTH_SHORT).show()
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

                        override fun getBody(): ByteArray {
                            return JSONObject(requestParams).toString().toByteArray()
                        }

                        override fun getBodyContentType(): String {
                            return "application/json"
                        }
                    }
                    networkQueue.addToRequestQueue(postRequest)
                }
            }
        }
    }

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
        myAdapter = MyChargeRecyclerViewAdapter(context!!, ArrayList(), this)
        charges_list.layoutManager = LinearLayoutManager(context)
        charges_list.adapter = myAdapter

        show_charges_button.setOnClickListener {
            val sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            payed = 0.0
            total = 0.0
            if (sharedPref != null) {
                val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")

                val month = spinner_months.selectedItem.toString()
                val year = spinner_years.selectedItem.toString()

                val networkQueue = NetworkQueue.getInstance(context)
                val url = Urls.BASE.value
                val path = "${Urls.CHARGES.value}?filter={\"where\":{\"month\":\"$month\",\"year\":\"$year\"}, \"include\":[\"person\"]}"

                val getRequest = object : JsonArrayRequest(Method.GET, url + path, null,
                        Response.Listener { response ->
                            processResponse(response, context)
                            charges_total_payed.text = getPayedLabel(context)
                        },
                        Response.ErrorListener {
                            println("----------------------------------------------------------------")
                            println("ERROR!!!")
                            println(it)
                            println("----------------------------------------------------------------")
                            charges_total_payed.text = getPayedLabel(context)
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

    private fun processResponse(response: JSONArray, context: Context) {
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

            payed += amountPayed
            total += amountPerson

            val item = ChargeItem(id.toString(),
                    amountPerson, amountPayed, date, name, preferredPayment)
            items.add(item)
        }

        val sortedList = items.sortedWith(compareBy({ it.amountPayed }, { it.name }))
        myAdapter?.addAll(sortedList)
    }

    private fun getPayedLabel(context: Context): String? {
        if (total == 0.0 && payed == 0.0) {
            return ""
        }
        val nf = NumberFormat.getInstance()
        return context.getString(R.string.charges_total_payed, nf.format(payed), nf.format(total))
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
