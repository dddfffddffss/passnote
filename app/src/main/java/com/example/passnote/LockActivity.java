package com.example.passnote;

import androidx.biometric.BiometricPrompt;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.passnote.util.pass_data;

import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        EditText e = findViewById(R.id.app_pass);
        Button b = findViewById(R.id.pass_in);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.v("가가", (String) errString);
                Toast.makeText(getApplicationContext(),"지문 인증 오류!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "지문 인증 실패!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "인증 성공!", Toast.LENGTH_SHORT).show();
                Intent lock = new Intent(getApplicationContext(), MainActivity.class);
                lock.putExtra("is_success",true);
                startActivity(lock.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finishActivity(123);
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해주세요.")
                .setNegativeButtonText("취소")
                .build();
        biometricPrompt.authenticate(promptInfo);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lock = new Intent(getApplicationContext(), MainActivity.class);
                pass_data pass_data = new pass_data(getApplicationContext());
                if(pass_data.get_password().equals(e.getText().toString())) {
                    lock.putExtra("is_success",true);
                    startActivity(lock);
                } else {
                    Toast.makeText(getApplicationContext(), "인증 실패!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
