package com.prit.ark.xpenses;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ARK on 01-11-2016.
 */

public class Utils {
    public static void displayMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
    public static String encodeEmail(String email){
        return email.replace('.', ',');
    }

    public static String decodeEmail(String email){
        return email.replace(',', '.');
    }

}





