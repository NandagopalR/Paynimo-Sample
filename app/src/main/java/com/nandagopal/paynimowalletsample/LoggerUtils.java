package com.nandagopal.paynimowalletsample;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by nandagopal on 8/28/16.
 */
public class LoggerUtils {

    public static void exceptionHandler(Context context, Exception exception) {
        Toast.makeText(context, "Message - " + exception.getMessage() + "\n Cause- " + exception.getCause(), Toast.LENGTH_LONG).show();
    }

}
