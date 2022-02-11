package com.app.fypfinal.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.R;
import com.app.fypfinal.models.UserModel;
import com.app.fypfinal.utils.DialogUtils;
import com.app.fypfinal.utils.Utils;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PostVerificationCode extends AppCompatActivity implements Info {

    EditText etVerCode;
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String verCodeBySystem;
    Dialog dgLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_verification_code);
        firebaseAuth = FirebaseAuth.getInstance();
        etVerCode = findViewById(R.id.et_ver_code);
        dgLoading = new Dialog(this);

        DialogUtils.initLoadingDialog(dgLoading);
        initCallBack();
        sendVerificationCode();

    }

    private void initCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                String code = credential.getSmsCode();
                if (code != null) {
                    verify(code);
                }
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verCodeBySystem = verificationId;
            }
        };

    }

    private void verify(String code) {
        dgLoading.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verCodeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }


    private void sendVerificationCode() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(Registration.userModel.getPhoneNumber())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public void back(View view) {
        finish();
    }

    public void verify(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verCodeBySystem,
                etVerCode.getText().toString().trim());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    dgLoading.dismiss();
                    if (task.isSuccessful()) {
                        Log.i(TAG, "signInWithPhoneAuthCredential: SUCCESS");
                        initUserData(Registration.userModel);

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });

    }

    private void initUserData(UserModel userModel) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(Registration.userModel.getEmail(), Registration.strEtPassword)
                .addOnCompleteListener(task -> {
                    dgLoading.show();
                    FirebaseDatabase.getInstance().getReference()
                            .child(USER_NODE)
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .setValue(userModel)
                            .addOnCompleteListener(task1 -> new Handler().postDelayed(() -> {
                                dgLoading.dismiss();
                                if (task1.isSuccessful()) {
                                    Utils.userModel = userModel;
                                    initDashCheck();
                                } else {
                                    Toast.makeText(PostVerificationCode.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                    if (task1.getException() != null)
                                        Log.i(TAG, "initUserData: " + Objects.requireNonNull(task1.getException()).getMessage());
                                }
                            }, 500));
                });
    }

    private void initDashCheck() {
        if (Utils.userModel.getType().equals(SALOON_MANAGER)) {
//            startActivity(new Intent(this, SaloonManagerActivity.class));
            LoginActivity.context.finish();
            Registration.context.finish();
            finish();
            return;
        }

        startActivity(new Intent(this, UserDashboard.class));
        Registration.context.finish();
        LoginActivity.context.finish();
        finish();
    }

}