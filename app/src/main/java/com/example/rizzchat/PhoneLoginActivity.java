package com.example.rizzchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private Button SendVerificationCodeButton, VerifyButton;
    private EditText InputPhoneNumber, InputVerificationCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean isVerificationInProgress = false;

    private Timer resendOtpTimer;
    private int resendOtpCountdown = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mAuth = FirebaseAuth.getInstance();

        SendVerificationCodeButton = findViewById(R.id.send_verification_code_button);
        VerifyButton = findViewById(R.id.verify_button);
        InputPhoneNumber = findViewById(R.id.phone_number_input);
        InputVerificationCode = findViewById(R.id.verification_code_input);
        loadingBar = new ProgressDialog(this);

        SendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = InputPhoneNumber.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneNumberVerification(phoneNumber);
                }
            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String verificationCode = InputVerificationCode.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(PhoneLoginActivity.this, "Please enter the verification code first", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(verificationCode);
                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please enter correct phone number with your country code", Toast.LENGTH_SHORT).show();
                resetUI();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent", Toast.LENGTH_SHORT).show();
                showVerificationUI();
                startResendOtpTimer();
            }
        };
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        loadingBar.setTitle("Phone Verification");
        loadingBar.setMessage("Please wait, while we are authenticating your phone...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                callbacks);
    }

    private void verifyPhoneNumberWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            SendUserToMainActivity();
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            resetUI();
                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void showVerificationUI() {
        SendVerificationCodeButton.setVisibility(View.INVISIBLE);
        InputPhoneNumber.setVisibility(View.INVISIBLE);
        VerifyButton.setVisibility(View.VISIBLE);
        InputVerificationCode.setVisibility(View.VISIBLE);
    }

    private void resetUI() {
        SendVerificationCodeButton.setVisibility(View.VISIBLE);
        InputPhoneNumber.setVisibility(View.VISIBLE);
        VerifyButton.setVisibility(View.INVISIBLE);
        InputVerificationCode.setVisibility(View.INVISIBLE);
    }

    private void startResendOtpTimer() {
        resendOtpTimer = new Timer();
        resendOtpTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resendOtpCountdown--;
                        if (resendOtpCountdown <= 0) {
                            resendOtpTimer.cancel();
                            SendVerificationCodeButton.setEnabled(true);
                            SendVerificationCodeButton.setText("Resend OTP");
                            // Set text color to black
                            SendVerificationCodeButton.setTextColor(Color.BLACK);
                        } else {
                            SendVerificationCodeButton.setEnabled(false);
                            SendVerificationCodeButton.setText("Resend OTP in " + resendOtpCountdown + " sec");
                            // Set text color to black
                            SendVerificationCodeButton.setTextColor(Color.BLACK);
                        }
                    }
                });
            }
        }, 0, 1000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendOtpTimer != null) {
            resendOtpTimer.cancel();
        }
    }
}
