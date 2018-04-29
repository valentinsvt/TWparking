package com.lzm.svt.twparking.modules.charges


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
import com.lzm.svt.twparking.modules.charges.ChargesListFragment.OnListFragmentInteractionListener
import com.lzm.svt.twparking.modules.charges.charge.ChargeItem
import kotlinx.android.synthetic.main.fragment_charge_item.view.*

class MyChargeRecyclerViewAdapter(
        private val context: Context,
        private val mValues: List<ChargeItem>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyChargeRecyclerViewAdapter.ViewHolder>() {
    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as ChargeItem
            mListener?.onChargePressed(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_charge_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.nameView.text = context.getString(R.string.charges_card_person_name, item.name)

        if (item.amountPayed == 0.0) {
            holder.owesLayout.visibility = View.VISIBLE
            holder.payedLayout.visibility = View.GONE

            holder.view.setCardBackgroundColor(context.getColor(R.color.charge_owed))
            holder.imageView.setBackgroundResource(R.drawable.ic_owed_black_24dp)

            holder.owesAmountView.text = context.getString(R.string.charges_card_amount_owed, item.amountPerson)
        } else {
            holder.payedLayout.visibility = View.VISIBLE
            holder.owesLayout.visibility = View.GONE

            holder.view.setCardBackgroundColor(context.getColor(R.color.charge_payed))
            holder.imageView.setBackgroundResource(R.drawable.ic_payed_black_24dp)

            holder.payedAmountView.text = context.getString(R.string.charges_card_amount_payed, item.amountPayed, item.date)
        }

        with(holder.mView) {
            tag = item
//            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val view: CardView = mView as CardView
        val nameView: TextView = mView.item_name
        val imageView: ImageView = mView.item_image

        val owesLayout: ConstraintLayout = mView.layout_person_owes
        val owesAmountView: TextView = mView.charge_owes_amount

        val payedLayout: ConstraintLayout = mView.layout_person_payed
        val payedAmountView: TextView = mView.charge_payed_amount
    }
}
