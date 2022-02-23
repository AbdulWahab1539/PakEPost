package com.app.fypfinal.utils;

import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.models.UserModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils implements Info {
    public static UserModel userModel;
    public static String token = "Token 91114b9ccf0ebd859096dcc05a3a919af878948a";

    public static boolean validEt(EditText etUserName, String strEtUserName) {
        if (strEtUserName.isEmpty()) {
            etUserName.setError("Field Empty");
            return false;
        }
        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean validatePhoneNumber(String phNumber) {
        Pattern mobNO = Pattern.compile(NUMBER_REGEX);
        Matcher matcher = mobNO.matcher(phNumber);
        return matcher.find();
    }
}
