package com.nandagopal.paynimowalletsample.wallet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nandagopal.paynimowalletsample.R;
import com.nandagopal.paynimowalletsample.base.BaseActivity;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;

/**
 * Created by nandagopal on 8/28/16.
 */
public class CheckoutActivity extends BaseActivity {

    private static final String TAG = "CheckoutActivity";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.merchant_activity_checkout);

        creatingCheckOutObjects();

    }

    private void creatingCheckOutObjects() {
        // Creating Checkout Object
        Checkout checkout = new Checkout();

        // setting values to checkout object
        checkout.setMerchantIdentifier("T45539");  //where T1234 is the merchant Code and will be provided by TPSL
        checkout.setTransactionIdentifier("TXN001"); //where TXN001 is the Merchant transaction identifier (alphanumeric no special character allowed)
        checkout.setTransactionReference("ORD0001"); //where ORD0001 is the Merchant transaction reference number
        checkout.setTransactionType("Sale"); //Transaction type
        checkout.setTransactionSubType("Debit"); //Transaction subtype
        checkout.setTransactionCurrency("INR"); //CURRENCY
        checkout.setTransactionAmount("1.00"); //Transaction amount
        checkout.setTransactionDateTime("27-06-2016"); //Transaction date

        // setting Consumer fields values
        checkout.setConsumerIdentifier("User1"); //Consumer Identifier, set this value as application user name
        checkout.setConsumerEmailID("test@gmail.com"); //Consumer email id
        checkout.setConsumerMobileNumber("7620656789"); //Consumer mobile number
        checkout.setConsumerAccountNo(""); //Default value "", leave it blank

        // setting Consumer Cart Item
        checkout.addCartItem("ProductID", "ProductAmount", "ProductSurchargeOrDiscountAmount", "CommisionAmount",
                "ProductSKU", "ProductReference", "ProductDescriptor", "ProductProviderID");

        // setting Standing Instruction Payment fields’ values
        // ACTION Y for SI enabled from merchant end and N for SI disabled
        checkout.setPaymentInstructionAction("Y");
        // Amount type -> Fixed - F, Maximum - M
        checkout.setPaymentInstructionType("F");
        // Max amount
        checkout.setPaymentInstructionLimit("1000.00");

        // Payment Frequency
        // DAIL - Daily, Week - Weekly, MNTH - Monthly,

        checkout.setPaymentInstructionFrequency("ADHO");
        // Debit start date, format -> 'DD-MM-YYYY'
        checkout.setPaymentInstructionStartDateTime("08-07-2016");
        // Debit end date, format -> 'DD-MM-YYYY'
        checkout.setPaymentInstructionEndDateTime("25-12-2051");

        //Case 3 (a): Data Capturing Page at Merchant End For - New Card

        checkout.setTransactionMerchantInitiated("Y");
        checkout.setPaymentInstrumentIdentifier("4111111111111111");
        checkout.setPaymentInstrumentExpiryMonth("02");
        checkout.setPaymentInstrumentExpiryYear("2019");
        checkout.setPaymentInstrumentVerificationCode("123");
        checkout.setPaymentInstrumentHolderName("Sumit Sharma");
        checkout.setTransactionIsRegistration("Y");

        //Case 3 (b): Data Capturing Page at Merchant End For - Saved Card

        checkout.setTransactionMerchantInitiated("Y");
        checkout.setPaymentInstrumentToken("123234");
        checkout.setPaymentInstrumentVerificationCode("123");


        //Case 3 (c): Data Capturing Page at Merchant End For – Netbanking/Wallets/EMI/CashCards

        checkout.setTransactionMerchantInitiated("Y");
        checkout.setPaymentMethodToken("123234");

        callingPaymentActivity(checkout);

    }

    private void callingPaymentActivity(Checkout checkout) {
        Intent authIntent = new Intent(this, PaymentModesActivity.class);

// Checkout Object
        Log.d("Checkout Request Object",
                checkout.getMerchantRequestPayload().toString());

        authIntent.putExtra(PaymentActivity.ARGUMENT_DATA_CHECKOUT,
                checkout);
// Public Key
        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY,
                "1234-6666-6789-56");
// Requested Payment Mode
        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                PaymentActivity.PAYMENT_METHOD_DEFAULT);

        startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PaymentActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == PaymentActivity.RESULT_OK) {
                Log.d(TAG, "Result Code :" + RESULT_OK);
                if (data != null) {

                    try {
                        Checkout checkout_res = (Checkout) data
                                .getSerializableExtra(PaymentActivity.ARGUMENT_DATA_CHECKOUT);
                        Log.d("Checkout Response Obj", checkout_res
                                .getMerchantResponsePayload().toString());

                        String transactionType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getType();
                        String transactionSubType = checkout_res.
                                getMerchantRequestPayload().getTransaction().getSubType();
                        if (transactionType != null && transactionType.equalsIgnoreCase(PaymentActivity.TRANSACTION_TYPE_PREAUTH)
                                && transactionSubType != null && transactionSubType
                                .equalsIgnoreCase(PaymentActivity.TRANSACTION_SUBTYPE_RESERVE)) {
                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload()
                                    .getPaymentMethod().getPaymentTransaction()
                                    .getStatusCode().equalsIgnoreCase(PaymentActivity.TRANSACTION_STATUS_PREAUTH_RESERVE_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0200 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getInstruction().
                                        getStatusCode().equalsIgnoreCase("")) {
                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0200 means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>",
                                            "SI Transaction Not Initiated");
                                }

                            } // Transaction Completed and Got FAILURE

                            else {
                                // some error from bank side
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(),
                                        "Transaction Status - Failure",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            // Transaction Completed and Got SUCCESS
                            if (checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                                    PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {
                                Toast.makeText(getApplicationContext(), "Transaction Status - Success", Toast.LENGTH_SHORT).show();
                                Log.v("TRANSACTION STATUS=>", "SUCCESS");

                                /**
                                 * TRANSACTION STATUS - SUCCESS (status code
                                 * 0300 means success), NOW MERCHANT CAN PERFORM
                                 * ANY OPERATION OVER SUCCESS RESULT
                                 */

                                if (checkout_res.getMerchantResponsePayload().
                                        getPaymentMethod().getPaymentTransaction().
                                        getInstruction().getStatusCode()
                                        .equalsIgnoreCase("")) {
                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0300 means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>",
                                            "SI Transaction Not Initiated");
                                } else if (checkout_res.getMerchantResponsePayload()
                                        .getPaymentMethod().getPaymentTransaction()
                                        .getInstruction()
                                        .getStatusCode().equalsIgnoreCase(
                                                PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {

                                    /**
                                     * SI TRANSACTION STATUS - SUCCESS (status
                                     * code 0300 means success)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                                } else {
                                    /**
                                     * SI TRANSACTION STATUS - Failure (status
                                     * code OTHER THAN 0300 means failure)
                                     */
                                    Log.v("TRANSACTION SI STATUS=>", "FAILURE");
                                }

                            } // Transaction Completed and Got FAILURE
                            else {
                                // some error from bank side
                                Log.v("TRANSACTION STATUS=>", "FAILURE");
                                Toast.makeText(getApplicationContext(),
                                        "Transaction Status - Failure",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                        String result = "StatusCode : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusCode()
                                + "\nStatusMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getStatusMessage()
                                + "\nErrorMessage : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getErrorMessage()
                                + "\nAmount : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getAmount()
                                + "\nDateTime : " + checkout_res.
                                getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getDateTime()
                                + "\nMerchantTransactionIdentifier : "
                                + checkout_res.getMerchantResponsePayload()
                                .getMerchantTransactionIdentifier()
                                + "\nIdentifier : " + checkout_res
                                .getMerchantResponsePayload().getPaymentMethod()
                                .getPaymentTransaction().getIdentifier();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == PaymentActivity.RESULT_ERROR) {
                Log.d(TAG, "got an error");

                if (data.hasExtra(PaymentActivity.RETURN_ERROR_CODE) &&
                        data.hasExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION)) {
                    String error_code = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_CODE);
                    String error_desc = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION);

                    Toast.makeText(getApplicationContext(), " Got error :"
                            + error_code + "--- " + error_desc, Toast.LENGTH_SHORT)
                            .show();
                    Log.d(TAG + " Code=>", error_code);
                    Log.d(TAG + " Desc=>", error_desc);

                }

            } else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Transaction Aborted by User",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User pressed back button");

            }
        }
    }

}
