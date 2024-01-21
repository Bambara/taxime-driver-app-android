package com.ynr.taximedriver.wallet;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ynr.taximedriver.R;

import java.util.List;

public class RechargeChooseDialog extends Dialog implements View.OnClickListener {
    public Activity activity;
    public Dialog dialog;
    public Button btnRechargeAccount;
    public WalletFragment walletFragment;
    private EditText etRechargeAmount;
    private ImageView cancel_image;


    public RechargeChooseDialog(Activity activity, WalletFragment walletFragment) {
        super(activity);
        this.activity = activity;
        this.walletFragment = walletFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recharge_choose_dialog);
        btnRechargeAccount = findViewById(R.id.btnRechargeAccount);
        etRechargeAmount = findViewById(R.id.etRechargeAmount);
        cancel_image = findViewById(R.id.cancel_image);

        btnRechargeAccount.setOnClickListener(this);
        cancel_image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRechargeAccount:
//                Intent intent = new Intent(getContext(), RechargeByAccountActivity.class);
//                activity.startActivity(intent);
                String amount = etRechargeAmount.getText().toString();

                if (!amount.equals("")) {
                    walletFragment.topUpWallet(Double.parseDouble(amount));
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Please enter any amount to recharge your account !", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.cancel_image:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
