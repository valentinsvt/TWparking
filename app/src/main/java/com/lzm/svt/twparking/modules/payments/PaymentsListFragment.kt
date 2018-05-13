package com.lzm.svt.twparking.modules.payments

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
import com.lzm.svt.twparking.Utils
import com.lzm.svt.twparking.Utils.Companion.formatNumber
import com.lzm.svt.twparking.modules.payments.payment.PaymentItem
import kotlinx.android.synthetic.main.fragment_payment_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class PaymentsListFragment : Fragment(), OnPaymentClickedInteractionListener {

    private var myAdapter: MyPaymentRecyclerViewAdapter? = null
    private var columnCount = 1
    private var payed = 0.0
    private var total = 0.0

    override fun onPaymentPressed(item: PaymentItem?) {
        val activity = this.context
        if (item != null && activity != null) {
            if (item.amountPayed == 0.0) {
                val sharedPref = activity.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                if (sharedPref != null) {
                    val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")
                    val networkQueue = NetworkQueue.getInstance(activity)
                    val path = "${Urls.PAYMENTS.value}/${item.id}"

                    val url = Urls.BASE.value

                    val dateFormat = "yyyy-MM-dd"
                    val sdf = SimpleDateFormat(dateFormat, Locale.US)
                    val today = Calendar.getInstance().time
                    val date = sdf.format(today)

                    val requestParams = HashMap<String, String>()
                    requestParams["amountPayed"] = "${item.amount}"
                    requestParams["date"] = date

                    val postRequest = object : JsonObjectRequest(Method.PATCH, url + path, null,
                            Response.Listener {
                                Toast.makeText(activity, "Marcado ${item.name} como pagado", Toast.LENGTH_LONG).show()
                                item.amountPayed = item.amount
                                payed += item.amountPayed
                                payments_total_payed.text = getPayedLabel(activity)
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
        return inflater.inflate(R.layout.fragment_payment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val months = Utils.getMonths()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)

        val context = this.context

        populateSpinners(months, years)
        myAdapter = MyPaymentRecyclerViewAdapter(context!!, ArrayList(), this)
        payments_list.layoutManager = LinearLayoutManager(context)
        payments_list.adapter = myAdapter

        show_payments_button.setOnClickListener {
            val sharedPref = context.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            payed = 0.0
            total = 0.0
            if (sharedPref != null) {
                val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")

                val month = spinner_months.selectedItem.toString()
                val year = spinner_years.selectedItem.toString()

                val networkQueue = NetworkQueue.getInstance(context)
                val url = Urls.BASE.value
                val path = "${Urls.PAYMENTS.value}?filter={\"where\":{\"month\":\"$month\",\"year\":\"$year\"}, \"include\":[\"owner\"]}"

                val getRequest = object : JsonArrayRequest(Method.GET, url + path, null,
                        Response.Listener { response ->
                            processResponse(response, context)
                            payments_total_payed.text = getPayedLabel(context)
                        },
                        Response.ErrorListener {
                            println("----------------------------------------------------------------")
                            println("ERROR!!!")
                            println(it)
                            println("----------------------------------------------------------------")
                            payments_total_payed.text = getPayedLabel(context)
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
        val items: MutableList<PaymentItem> = ArrayList()
        for (i in 0..(response.length() - 1)) {
            val payment = response.getJSONObject(i)
            val id = payment.getInt("id")
            val amount = payment.getDouble("amount")
            val amountPayed = payment.getDouble("amountPayed")
            val date = payment.getString("date")
            val person = payment.getJSONObject("owner")
            val name = person.getString("name")

            payed += amountPayed
            total += amount

            val item = PaymentItem(id.toString(), amount, amountPayed, date, name)
            items.add(item)
        }

        val sortedList = items.sortedWith(compareBy({ it.amountPayed }, { it.name }))
        myAdapter?.addAll(sortedList)
    }

    private fun getPayedLabel(context: Context): String? {
        if (total == 0.0 && payed == 0.0) {
            return ""
        }
        return context.getString(R.string.payments_total_payed, formatNumber(payed), formatNumber(total))
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
                PaymentsListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
