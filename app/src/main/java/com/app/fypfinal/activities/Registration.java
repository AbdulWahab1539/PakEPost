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
import com.app.fypfinal.models.UserModel;
import com.app.fypfinal.mvvm.mvvmutils.MVVMUtils;
import com.app.fypfinal.mvvm.pojo.PostProfilePojo;
import com.app.fypfinal.mvvm.pojo.RegResponsePojo;
import com.app.fypfinal.mvvm.response.GenericResponse;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;

public class Registration extends AppCompatActivity implements Info {

    public static String verId;
    public static UserModel userModel;
    public static Activity context;
    public static String strEtPassword;
    boolean isPassVisible = false;
    EditText etUserName;
    EditText etPhone;
    EditText etPassword;
    EditText etConfirmPassword;
    EditText etFirstName;
    EditText etLastName, etEmail, etCnic;
    String strEtFirstName;
    String strEtLastName;
    String strEtUserName;
    String strEtEmail;
    String strEtPhone;
    String strEtConfirmPassword, strEtCnic;
    Dialog dgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = this;
        initViews();
        dgLoading = new Dialog(this);
        DialogUtils.initLoadingDialog(dgLoading);
    }

    public void showPassword(View view) {
        if (!isPassVisible) {
            etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            isPassVisible = true;
        } else {
            etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            isPassVisible = false;
        }
    }


    private void castStrings() {
        Log.i(TAG, "castStrings: ");
        strEtFirstName = etFirstName.getText().toString();
        strEtLastName = etLastName.getText().toString();
        strEtUserName = etUserName.getText().toString();
        strEtPhone = etPhone.getText().toString();
        strEtPassword = etPassword.getText().toString();
        strEtConfirmPassword = etConfirmPassword.getText().toString();
        strEtEmail = etEmail.getText().toString();
        strEtCnic = etCnic.getText().toString();
    }

    private void initViews() {
        etUserName = findViewById(R.id.et_user_name);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_pass);
        etConfirmPassword = findViewById(R.id.et_confirm_pass);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etCnic = findViewById(R.id.et_cnic);
        etEmail = findViewById(R.id.et_email);
    }

    public void back(View view) {
        finish();
    }

    public void SignUp(View view) {
        castStrings();
        if (!Utils.validEt(etFirstName, strEtFirstName)) return;
        if (!Utils.validEt(etLastName, strEtLastName)) return;
        if (!Utils.validEt(etUserName, strEtUserName)) return;
        if (!Utils.validEt(etPhone, strEtPhone)) return;
        if (!Utils.validEt(etEmail, strEtEmail)) return;
        if (!strEtPassword.equals(strEtConfirmPassword)) return;
        if (!Utils.isValidEmail(strEtEmail)) return;
        if (!Utils.validEt(etCnic, strEtCnic)) return;
        if (!Utils.validatePhoneNumber(strEtPhone)) {
            etPhone.setError("Phone number is wrong");
            return;
        }
        if (strEtCnic.length() < 13) {
            etCnic.setError("please check Cnic number");
            return;
        }
        PostProfilePojo postProfilePojo = new PostProfilePojo(
                strEtFirstName, strEtLastName, strEtUserName, strEtEmail, strEtPhone, strEtPassword,
                strEtConfirmPassword);

        dgLoading.show();
        MVVMUtils.getViewModelRepo(this)
                .postUser(postProfilePojo)
                .observe(this, this::initRegistrationResponse);
    }

    private void initRegistrationResponse(GenericResponse<RegResponsePojo> genericResponse) {
        dgLoading.dismiss();
        if (genericResponse.isSuccessful()) {
            Log.i(TAG, "initRegistrationResponse: " + genericResponse.getResponse().getKey());
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else
            MVVMUtils.initErrMessages(this, genericResponse.getErrorMessages(), genericResponse.getResponseCode());
    }
}