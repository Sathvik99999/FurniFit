package com.furnifit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private RadioGroup genderRadioGroup;
    private EditText age;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.name);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        age = findViewById(R.id.age);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String name_txt = name.getText().toString().trim();
            String age_txt = age.getText().toString().trim();
            String email_txt = email.getText().toString().trim();
            String password_txt = password.getText().toString().trim();
            String confirm_password_txt = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name_txt)) {
                Toast.makeText(RegisterActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email_txt)) {
                Toast.makeText(RegisterActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password_txt)) {
                Toast.makeText(RegisterActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password_txt.equals(confirm_password_txt)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email_txt, password_txt)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                int selectedId = genderRadioGroup.getCheckedRadioButtonId();
                                RadioButton selectedRadioButton = findViewById(selectedId);
                                String gender_txt = selectedRadioButton.getText().toString();

                                Map<String, Object> user_data = new HashMap<>();
                                user_data.put("name", name_txt);
                                user_data.put("age", age_txt);
                                user_data.put("gender", gender_txt);
                                user_data.put("email", email_txt);

                                db.collection("users").document(userId)
                                        .set(user_data)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(RegisterActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
