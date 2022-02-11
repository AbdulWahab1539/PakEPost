package com.app.fypfinal.utils;

import android.widget.EditText;

import com.app.fypfinal.models.UserModel;


public class Utils {
    public static UserModel userModel;

    public static boolean validEt(EditText etUserName, String strEtUserName) {
        if (strEtUserName.isEmpty()) {
            etUserName.setError("Field Empty");
            return false;
        }
        return true;
    }
}
