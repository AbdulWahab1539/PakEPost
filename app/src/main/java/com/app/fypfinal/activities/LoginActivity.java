package com.app.fypfinal.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.LoginPojo;
import com.app.fypfinal.mvvm.pojo.ProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.SharedPerfUtils;
import com.app.fypfinal.utils.Utils;


public class LoginActivity extends AppCompatActivity implements Info {

    public static Activity context;
    EditText etEmail;
    EditText etPassword;
    String strEtEmail;
    String strEtPassword;
    boolean isPassVisible = false;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPerfUtils.getStringSharedPrefs(this, PREF_ACCESS_TOKEN) != null
                && !SharedPerfUtils.getStringSharedPrefs(this, PREF_ACCESS_TOKEN).isEmpty()) {
            startActivity(new Intent(this, UserDashboard.class));
            finish();
        } else initViews();
    }

    private void initViews() {
        context = this;

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pass);

        loadingDialog = new Dialog(this);
        DialogUtils.initLoadingDialog(loadingDialog);
    }

    public void signUp(View view) {
        startActivity(new Intent(this, Registration.class));
    }

    public void showPassword(View view) {
        if (!isPassVisible) {
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPassVisible = true;
        } else {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPassVisible = false;
        }

    }

    public void ForgotPassword(View view) {
//        TODO : RESET PASSWORD
    }

    private void castStrings() {
        strEtEmail = etEmail.getText().toString();
        strEtPassword = etPassword.getText().toString();
    }

    private boolean isEverythingValid() {
        if (!Utils.validEt(etEmail, strEtEmail))
            return false;
        return Utils.validEt(etPassword, strEtPassword);
    }

    public void Login(View view) {
        castStrings();
        if (!isEverythingValid()) return;
        loadingDialog.show();
        LoginPojo loginPojo = new LoginPojo(strEtEmail, strEtPassword);
        MVVMUtils.getViewModelRepo(this)
                .loginUser(loginPojo)
                .observe(this, this::initLoginResponse);
    }

    private void initLoginResponse(GenericResponse<RegResponsePojo> genericResponse) {
        if (genericResponse.isSuccessful()) {
            Log.i(TAG, "initLoginResponse: " + genericResponse.getResponse().getKey());
            SharedPerfUtils.putStringSharedPrefs(this, genericResponse.getResponse().getKey(), PREF_ACCESS_TOKEN);
            Utils.token = genericResponse.getResponse().getKey();
            MVVMUtils.getViewModelRepo(this).getProfile(this).observe(this, this::initProfileResponse);
        } else
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
        loadingDialog.dismiss();
    }

    private void initProfileResponse(GenericResponse<ProfilePojo> genericResponse) {
        if (genericResponse.isSuccessful()) {
            if (genericResponse.getResponse().getIsCustomer())
                startActivity(new Intent(this, UserDashboard.class));
            else
                startActivity(new Intent(this, PostmanDashboard.class));
            finish();
        } else
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
    }
}