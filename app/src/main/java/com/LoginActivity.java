// LoginActivity.java
// شاشة تسجيل الدخول - البريد الإلكتروني وكلمة المرور عبر Firebase Auth

package com.example.medicalbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // ✅ تعريف عناصر الواجهة
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;
    private ProgressBar progressBar;

    // ✅ Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ربط العناصر بالـ XML
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvGoRegister= findViewById(R.id.tvGoRegister);
        progressBar = findViewById(R.id.progressBar);

        // تهيئة Firebase
        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // ✅ عند الضغط على زر "تسجيل الدخول"
        btnLogin.setOnClickListener(v -> loginUser());

        // ✅ عند الضغط على "إنشاء حساب جديد"
        tvGoRegister.setOnClickListener(v ->
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void loginUser() {
        // نأخذ النص من حقول الإدخال
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // ✅ التحقق من أن الحقول غير فارغة
        if (email.isEmpty()) {
            etEmail.setError("أدخلي البريد الإلكتروني");
            return;
        }
        if (password.isEmpty()) {
            etPassword.setError("أدخلي كلمة المرور");
            return;
        }

        // نُظهر دائرة التحميل ونُخفي الزر
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // ✅ تسجيل الدخول عبر Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                // ✅ نجح تسجيل الدخول - نجلب نوع المستخدم من Firestore
                String uid = authResult.getUser().getUid();

                db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        progressBar.setVisibility(View.GONE);

                        String role = document.getString("role");

                        if ("doctor".equals(role)) {
                            // طبيب
                            startActivity(new Intent(this, DoctorMainActivity.class));
                        } else {
                            // مريض
                            startActivity(new Intent(this, PatientMainActivity.class));
                        }
                        finish(); // نغلق شاشة تسجيل الدخول
                    });
            })
            .addOnFailureListener(e -> {
                // ❌ فشل تسجيل الدخول
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(this, "خطأ: البريد أو كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show();
            });
    }
}
