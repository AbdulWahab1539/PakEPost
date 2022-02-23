package com.app.fypfinal.mvvm.mvvmutils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.mvvm.vmrepo.ViewModelRepo;
import com.app.fypfinal.utils.SharedPerfUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MVVMUtils implements Info {
    public static ViewModelRepo getViewModelRepo(Activity context) {
        return new ViewModelProvider((ViewModelStoreOwner) context).get(ViewModelRepo.class);
    }

    public static String getToken(Activity context) {
        return "Token " + SharedPerfUtils.getStringSharedPrefs(context, PREF_ACCESS_TOKEN);
    }


    public static List<String> parseError(String response) {
        List<String> errorList = new ArrayList<>();
        try {
//            Log.i(TAG, "parseError: " + response);
            JSONObject jObjError = new JSONObject(response);
            initError("detail", errorList, jObjError);
            initError("message", errorList, jObjError);
            initError("non_field_errors", errorList, jObjError);
        } catch (JSONException e) {
//            e.printStackTrace();
            errorList.add(response);
//            Log.i(TAG, "parseError : EXCEPTION IN PARSING : ");
        }
        return errorList;
    }

    private static void initError(String key, List<String> errorList, JSONObject jObjError) {
        try {
            String err = (String) jObjError.getJSONArray(key).get(0);
            errorList.add(err);
        } catch (Exception e) {
            try {
                String err = jObjError.getString(key);
                errorList.add(err);
            } catch (JSONException jsonException) {
                // jsonException.printStackTrace();
            }
            //e.printStackTrace();
        }
    }

    public static void initErrMessages(Context context, List<String> messages, int code) {
        if (messages == null)
            return;
        for (String message : messages) {
            Toast.makeText(context,
                    code + " " + message,
                    Toast.LENGTH_SHORT).show();
        }
    }

}
