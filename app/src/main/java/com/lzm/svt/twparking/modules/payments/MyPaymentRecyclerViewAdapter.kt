package com.lzm.svt.twparking.modules.payments


import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lzm.svt.twparking.R
import com.lzm.svt.twparking.modules.payments.payment.PaymentItem
import kotlinx.android.synthetic.main.fragment_payment_item.view.*

class MyPaymentRecyclerViewAdapter(
        private val context: Context,
        private var mValues: List<PaymentItem>,
        private val mListener: OnPaymentClickedInteractionListener?)
    : RecyclerView.Adapter<MyPaymentRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PaymentItem
            mListener?.onPaymentPressed(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_payment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.nameView.text = context.getString(R.string.payments_card_owner_name, item.name)

        if (item.amountPayed == 0.0) {
            holder.owesLayout.visibility = View.VISIBLE
            holder.payedLayout.visibility = View.GONE

            holder.view.setCardBackgroundColor(context.getColor(R.color.color_payment_owed))
            holder.imageView.setBackgroundResource(R.drawable.ic_owed_black_24dp)

            holder.owesAmountView.text = context.getString(R.string.payments_card_amount_owed, item.amount)
        } else {
            holder.payedLayout.visibility = View.VISIBLE
            holder.owesLayout.visibility = View.GONE

            holder.view.setCardBackgroundColor(context.getColor(R.color.color_payment_payed))
            holder.imageView.setBackgroundResource(R.drawable.ic_payed_black_24dp)

            holder.payedAmountView.text = context.getString(R.string.payments_card_amount_payed, item.amountPayed, item.date)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size
    fun sort() {
        mValues = mValues.sortedWith(compareBy({ it.amountPayed }, { it.name }))
        this.notifyDataSetChanged()
    }

    fun addAll(newList: List<PaymentItem>) {
        mValues = newList
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val view: CardView = mView as CardView
        val nameView: TextView = mView.item_name
        val imageView: ImageView = mView.item_image

        val owesLayout: ConstraintLayout = mView.layout_person_owes
        val owesAmountView: TextView = mView.payment_owes_amount

        val payedLayout: ConstraintLayout = mView.layout_person_payed
        val payedAmountView: TextView = mView.payment_payed_amount
    }
}
