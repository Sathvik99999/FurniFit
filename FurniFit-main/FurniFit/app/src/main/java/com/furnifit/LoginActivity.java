package com.furnifit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailOrNumber;
    private EditText password;
    private Button submitButton;
    private TextView registerLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailOrNumber = findViewById(R.id.email_or_number);
        password = findViewById(R.id.password);
        submitButton = findViewById(R.id.submit_button);
        registerLink = findViewById(R.id.register_link);

        submitButton.setOnClickListener(v -> {
            String email_txt = emailOrNumber.getText().toString().trim();
            String password_txt = password.getText().toString().trim();

            if (TextUtils.isEmpty(email_txt)) {
                Toast.makeText(LoginActivity.this, "Enter email or number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password_txt)) {
                Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Developer cheat login
            if (email_txt.equals("1234567890") && password_txt.equals("furnifit")) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            mAuth.signInWithEmailAndPassword(email_txt, password_txt)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
