package com.nandagopal.paynimowalletsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nandagopal.paynimowalletsample.R;
import com.nandagopal.paynimowalletsample.base.BaseActivity;
import com.nandagopal.paynimowalletsample.wallet.activity.CheckoutActivity;

/**
 * Created by nandagopal on 8/28/16.
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private Button btnGoToWallet;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        (btnGoToWallet = (Button) findViewById(R.id.btnWallet)).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, CheckoutActivity.class));
    }
}
