package com.ynr.taximedriver.wallet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ynr.taximedriver.R
import kotlinx.android.synthetic.main.activity_recharge_by_account.*

private var btnBack: ImageView? = null
var bankAccountAdapter: BankAccountAdapter? = null

class RechargeByAccountActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge_by_account)

        //supportActionBar?.hide();

        btnBack!!.setOnClickListener {
            this.onBackPressed()
        }

        fabAdd.setOnClickListener {
            val intent = Intent(baseContext, AddBankAccountActivity::class.java)
            startActivity(intent)
        }
    }

    var accountList: List<AccountDetailItem> = listOf(
        AccountDetailItem(
            "D M B P Kumarasinghe",
            "Sampath Bank",
            "123456789",
        ), AccountDetailItem(
            "D M B P Kumarasinghe",
            "Sampath Bank",
            "123456789",
        ), AccountDetailItem(
            "D M B P Kumarasinghe",
            "Sampath Bank",
            "123456789",
        ), AccountDetailItem(
            "D M B P Kumarasinghe",
            "Sampath Bank",
            "123456789",
        ), AccountDetailItem(
            "D M B P Kumarasinghe",
            "Sampath Bank",
            "123456789",
        )
    )

    override fun onStart() {
        super.onStart()

        bankAccountAdapter = BankAccountAdapter(baseContext, accountList) { qoute ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, qoute)
            startActivity(shareIntent)
        }

        val lmCategories = LinearLayoutManager(baseContext)
        rvBankAccount.adapter = bankAccountAdapter
        rvBankAccount.layoutManager = lmCategories
    }

}