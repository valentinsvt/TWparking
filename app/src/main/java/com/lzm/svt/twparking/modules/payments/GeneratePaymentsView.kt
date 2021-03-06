package com.lzm.svt.twparking.modules.payments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.contracts.FragmentContracts
import kotlinx.android.synthetic.main.fragment_generate_payments.*

class GeneratePaymentsView : Fragment(), PaymentsContracts.PaymentsViewType {
    override fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private var listener: FragmentContracts.OnFragmentInteractionListener? = null
    var presenter: PaymentsContracts.PaymentsPresenterType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generate_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.onGeneratePaymentsViewCreated()
        generate_button.setOnClickListener {
            val sharedPref = context?.getSharedPreferences(getString(R.string.pref_file_key), Context.MODE_PRIVATE)
            if (sharedPref != null) {
                val token = sharedPref.getString(getString(R.string.pref_token_key), "NA")
                val dialogBuilder = AlertDialog.Builder(activity)
                dialogBuilder.setTitle(R.string.payments_generate_button)
                dialogBuilder.setMessage(R.string.confirmation_message)
                dialogBuilder.setPositiveButton(R.string.confirmation_continue_button, DialogInterface.OnClickListener { _, _ ->
                    presenter?.generateButtonPressed(month_spinner.selectedItem.toString(), year_spinner.selectedItem.toString(), token)
                })
                dialogBuilder.setNegativeButton(R.string.confirmation_cancel_button, DialogInterface.OnClickListener { _, _ -> })
                dialogBuilder.create().show()
            }
        }
    }

    override fun populateSpinners(months: Array<String>, years: Array<Int>) {
        month_spinner?.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, months)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }

        year_spinner?.let {
            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, years)
            adapter.setDropDownViewResource(R.layout.spinner)
            it.adapter = adapter
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentContracts.OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {

        @JvmStatic
        fun newInstance() = GeneratePaymentsView()
    }
}
