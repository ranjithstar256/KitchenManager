package kp.ran.kitchenmanager.jvaotp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;
import kp.ran.kitchenmanager.MainActivityKt;
import kp.ran.kitchenmanager.R;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String codeSent;
    PhoneAuthProvider.ForceResendingToken tokenone;
    Button btmsendotp;
    Button btnverifyotp;
    Button btnresend;
    EditText ednum, edotp;
    TextView tv1, tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btmsendotp = findViewById(R.id.button);
        btnverifyotp = findViewById(R.id.button2);
        btnresend = findViewById(R.id.button3);
        ednum = findViewById(R.id.editTextPhone);
        edotp = findViewById(R.id.editTextTextPersonName);
        tv1 = findViewById(R.id.textView);
        tv2 = findViewById(R.id.textView2);
        btmsendotp.setOnClickListener(view -> {
            Log.d("321abcd", "sendotp: " + "+91" + ednum.getText().toString());
            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber("+91" + ednum.getText().toString())       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(MainActivity.this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);      // OnVerificationStateChangedCallbacks


            Thread t = new Thread() {
                public void run() {
                    for (int i = 30; i > 0; i--) {
                        try {
                            int finalI = i;
                            runOnUiThread(() -> {
                                tv2.setText("Please Wait " + finalI + " seconds to Resend OTP");
                                if (finalI == 2) {
                                    btnresend.setVisibility(View.VISIBLE);
                                    btmsendotp.setVisibility(View.INVISIBLE);
                                    tv2.setVisibility(View.GONE);
                                }
                            });
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            t.start();
        });


        btnresend.setOnClickListener(view -> {
            resendVerificationCode("+91" + ednum.getText().toString(), tokenone);
        });
    }

    public void sgn(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, edotp.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            tokenone = forceResendingToken;
            Log.d("132abcd", "sendotp: " + s + "\n\n\n" + codeSent);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(MainActivity.this, "Completed!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            Log.d("321TAG", "onVerificationFailed: " + e.getMessage().toString());
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        tv1.setText("Success!!");
                        btnresend.setVisibility(View.GONE);
                        Intent i = new Intent(MainActivity.this, kp.ran.kitchenmanager.MainActivity.class);
                        i.putExtra("mobnumber", ednum.getText().toString());
                        i.putExtra("userid",mAuth.getUid());
                        Log.d("hgrsetgrrsdghr43434", "signInWithPhoneAuthCredential: "+mAuth.getUid().toString());
                        startActivity(i);

                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), "Incorrect Verification code", Toast.LENGTH_SHORT).show();
                            tv1.setText("RETRY Please !");
                            btnresend.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
}