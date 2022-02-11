package com.app.fypfinal.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.models.UserModel;
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
    EditText etLastName;
    String strEtFirstName;
    String strEtLastName;
    String strEtUserName;
    String strEtEmail;
    String strEtPhone;
    String strEtConfirmPassword;

    RadioButton rbSalonManager;
    RadioButton rbCustomer;
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void castStrings() {
        strEtFirstName = etFirstName.getText().toString();
        strEtLastName = etLastName.getText().toString();
        strEtUserName = etUserName.getText().toString();
        strEtPhone = etPhone.getText().toString();
        strEtPassword = etPassword.getText().toString();
        strEtConfirmPassword = etConfirmPassword.getText().toString();
    }

    private void initViews() {
        etUserName = findViewById(R.id.et_user_name);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_pass);
        etConfirmPassword = findViewById(R.id.et_confirm_pass);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        rbSalonManager = findViewById(R.id.rb_salon_manager);
        rbCustomer = findViewById(R.id.rb_customer);
        rbCustomer.setChecked(true);
    }

    public void back(View view) {
        finish();
    }

    public void SignUpWithPhone(View view) {
        castStrings();
        if (!Utils.validEt(etFirstName, strEtFirstName))
            return;

        if (!Utils.validEt(etLastName, strEtLastName))
            return;

        if (!Utils.validEt(etUserName, strEtUserName))
            return;

        if (!Utils.validEt(etPhone, strEtPhone))
            return;

        if (!strEtPassword.equals(strEtConfirmPassword))
            return;

        strEtEmail = "abc" + strEtPhone + "@email.com";

        Registration.userModel = new UserModel(strEtFirstName, strEtLastName, strEtUserName,
                strEtEmail,
                "+" + strEtPhone,
                CUSTOMER);

        startActivity(new Intent(this, PostVerificationCode.class));
    }


}