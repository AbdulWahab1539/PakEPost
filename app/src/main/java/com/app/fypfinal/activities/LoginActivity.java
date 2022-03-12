package com.app.fypfinal.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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

    EditText etEmail;
    EditText etPassword;
    String strEtEmail;
    String strEtPassword;
    boolean isPassVisible = false;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new Dialog(this);
        DialogUtils.initLoadingDialog(loadingDialog);

        if (SharedPerfUtils.getStringSharedPrefs(this, PREF_ACCESS_TOKEN) != null) initProfile();
        else initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_pass);
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
        Log.i(TAG, "initLoginResponse: ");
        if (genericResponse.isSuccessful()) {
            Log.i(TAG, "initLoginResponse: " + genericResponse.getResponse().getKey());
            SharedPerfUtils.putStringSharedPrefs(this, genericResponse.getResponse().getKey(), PREF_ACCESS_TOKEN);
            initProfile();
        } else
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
        loadingDialog.dismiss();
    }

    private void initProfile() {
        Log.i(TAG, "initProfile: ");
        loadingDialog.show();
        MVVMUtils.getViewModelRepo(this)
                .getProfile(this)
                .observe(this, this::initProfileResponse);
    }

    private void initProfileResponse(GenericResponse<ProfilePojo> genericResponse) {
        loadingDialog.dismiss();
        if (genericResponse.isSuccessful()) {
            Utils.profilePojo = genericResponse.getResponse();
            Log.i(TAG, "initProfileResponse: " + Utils.profilePojo.isCustomer() + Utils.profilePojo.isPostman());
            if (Utils.profilePojo.isCustomer()) {
                startActivity(new Intent(this, UserDashboard.class));
                finish();
            } else if (Utils.profilePojo.isPostman()) {
                startActivity(new Intent(this, PostmanDashboard.class));
                finish();
            } else {
                Toast.makeText(this, "The Credentials you have provided are not for user or postman",
                        Toast.LENGTH_SHORT).show();
                initViews();
            }
        } else if (genericResponse.getResponseCode() == 401) {
            SharedPerfUtils.putStringSharedPrefs(this, null, PREF_ACCESS_TOKEN);
            initViews();
        } else
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
    }
}