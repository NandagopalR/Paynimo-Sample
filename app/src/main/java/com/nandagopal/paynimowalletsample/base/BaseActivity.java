package com.nandagopal.paynimowalletsample.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    protected AppCompatActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mActivity = this;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showToast(String input) {
        Toast.makeText(mActivity, input, Toast.LENGTH_SHORT).show();
    }

    public void setHeaderTitle(String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    public void showBackButton(boolean status) {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(status);
    }

    public void hideSoftInput() {
        try {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean userEvent = false;
        try {
            View v = getCurrentFocus();
            userEvent = super.dispatchTouchEvent(event);
            if (v instanceof EditText) {
                View w = getCurrentFocus();
                int scr[] = new int[2];
                if (w != null) {
                    w.getLocationOnScreen(scr);
                }
                assert w != null;
                float x = event.getRawX() + w.getLeft() - scr[0];
                float y = event.getRawY() + w.getTop() - scr[1];
                if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft()
                        || x >= w.getRight()
                        || y < w.getTop()
                        || y > w.getBottom())) {
                    hideSoftInput();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userEvent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
