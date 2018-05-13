package com.lzm.svt.twparking.modules.charges


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.lzm.svt.twparking.NetworkQueue
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.Urls
import com.lzm.svt.twparking.Utils
import kotlinx.android.synthetic.main.fragment_mail.*
import org.json.JSONArray
import java.util.*

class MailFragment : Fragment() {
    private val url = Urls.BASE.value

    private var mail = ""
    private var month = ""
    private var year = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val months = Utils.getMonths()

        fab_send_mail.isEnabled = false

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = arrayOf(currentYear - 1, currentYear, currentYear + 1)
        populateSpinners(months, years)
        val context = this.context
        val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
                ?: return
        val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")

        val networkQueue = NetworkQueue.getInstance(context)
        show_mail_button.setOnClickListener {

            month = spinner_months.selectedItem.toString()
            year = spinner_years.selectedItem.toString()

            mail_contents.setText("")

            //{"where":{"month":"MAYO","year":2018,"amountPayed":0}, "include":["person"]}

            val path = "${Urls.CHARGES.value}?filter={\"where\":{\"month\":\"$month\",\"year\":\"$year\",\"amountPayed\":0}, \"include\":[\"person\"]}"
            val getChargesRequest = object : JsonArrayRequest(Method.GET, url + path, null,
                    Response.Listener { response ->
                        println(it)
                        fab_send_mail.isEnabled = true
                        getParams(month, year, token, networkQueue, response)
                    },
                    Response.ErrorListener {
                        println(it)
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
            networkQueue.addToRequestQueue(getChargesRequest)
        }

        fab_send_mail.setOnClickListener { v ->
            val to = "lmunda@thoughtworks.com"
            val subject = "Puestos para $month $year"
//            var message = mail
//            message += "asdf\tsafasdf\n"
//            message += "asdf\tsafasdf\n"
            var message = mail

            val intent = Intent(Intent.ACTION_SEND)
            val addressees = arrayOf(to)
            intent.putExtra(Intent.EXTRA_EMAIL, addressees)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.data = Uri.parse("mailto:")
//            intent.type = "message/rfc822"
            intent.setType("text/html")
            startActivity(Intent.createChooser(intent, "Select Email Sending App:"))
        }
    }

    private fun getTable(message: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(message).toString()
        }
    }

    private fun getParams(month: String, year: String, token: String, networkQueue: NetworkQueue, people: JSONArray) {
        val path = Urls.PARAMS.value
        val getParamsRequest = object : JsonArrayRequest(Method.GET, url + path, null,
                Response.Listener {
                    val res = it.getJSONObject(0)
                    var text = res.getString("mailTemplate")
                    text = text.replace("::mes::", "$month $year")

                    mail_contents.text = text
                    mail = text
                    for (i in 0..(people.length() - 1)) {
                        val person = people.getJSONObject(i)
                        val user = person.getJSONObject("person")
                        val name = user.getString("name")
                        val amount = person.getDouble("amountPerson")
                        mail += "<br/>${Utils.formatNumber(amount)} $name "
                    }
                    mail = getTable(mail)
                },
                Response.ErrorListener {
                    println(it)
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
        networkQueue.addToRequestQueue(getParamsRequest)
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
        @JvmStatic
        fun newInstance() =
                MailFragment().apply {
                }
    }
}
