package com.ynr.taximedriver.wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ynr.taximedriver.R
import kotlinx.android.synthetic.main.activity_on_trip.view.*

class BankAccountAdapter(
    val context: Context,
    val quotes: List<AccountDetailItem>,
    val onItemLongClick: (String) -> Unit
) :
    RecyclerView.Adapter<BankAccountAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bank_account_item, parent, false)
        val myViewHolder = MyViewHolder(view, onItemLongClick)

        return myViewHolder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindQuote(quotes[position])
        AnimationHelper.animate(holder.itemView)
    }

    override fun getItemCount(): Int {
        return quotes.count()
    }

    inner class MyViewHolder(itemView: View, onItemLongClick: (String) -> Unit) :
        RecyclerView.ViewHolder(itemView) {

        private val tvAccountName: TextView = itemView.findViewById<TextView>(R.id.tvAccountName)
        private val tvBank: TextView = itemView.findViewById<TextView>(R.id.tvBank)
        private val tvAccountNumber: TextView = itemView.findViewById<TextView>(R.id.tvAccountNumber)
        private var ivEdit: ImageView = itemView.findViewById<ImageView>(R.id.ivEdit)
        private var ivDelete: ImageView = itemView.findViewById<ImageView>(R.id.ivDelete)
        private var rbSelect: RadioButton = itemView.findViewById<RadioButton>(R.id.rbSelect)


        fun bindQuote(quotes: AccountDetailItem) {
            tvAccountName.text = quotes.accountName
            tvBank.text = quotes.bankName
            tvAccountNumber.text = quotes.accNumber
            ivEdit.setImageResource(R.drawable.ic_baseline_edit_24)
            ivDelete.setImageResource(R.drawable.ic_baseline_delete_24)
            rbSelect.radioGroup3
        }
    }
}