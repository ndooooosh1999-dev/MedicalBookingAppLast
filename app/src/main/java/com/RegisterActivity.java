// RegisterActivity.java
// شاشة إنشاء حساب جديد - للمريض أو الطبيب

package com.example.medicalbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etPhone;
    private EditText etSpecialty; // يظهر فقط للطبيب
    private RadioGroup rgRole;
    private RadioButton rbPatient, rbDoctor;
    private LinearLayout layoutDoctorFields; // حقول خاصة بالطبيب
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ربط العناصر
        etName           = findViewById(R.id.etName);
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        etPhone          = findViewById(R.id.etPhone);
        etSpecialty      = findViewById(R.id.etSpecialty);
        rgRole           = findViewById(R.id.rgRole);
        rbPatient        = findViewById(R.id.rbPatient);
        rbDoctor         = findViewById(R.id.rbDoctor);
        layoutDoctorFields = findViewById(R.id.layoutDoctorFields);
        btnRegister      = findViewById(R.id.btnRegister);
        progressBar      = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // ✅ عند اختيار نوع الحساب - نُظهر أو نُخفي حقل التخصص
        rgRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDoctor) {
                // اختار "طبيب" - نُظهر حقل التخصص
                layoutDoctorFields.setVisibility(View.VISIBLE);
            } else {
                // اختار "مريض" - نُخفي حقل التخصص
                layoutDoctorFields.setVisibility(View.GONE);
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // نجمع البيانات
        String name     = etName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone    = etPhone.getText().toString().trim();

        // تحديد نوع المستخدم
        String role = rbDoctor.isChecked() ? "doctor" : "patient";
        String specialty = rbDoctor.isChecked() ? etSpecialty.getText().toString().trim() : "";

        // ✅ التحقق من الحقول
        if (name.isEmpty())     { etName.setError("أدخلي اسمك");            return; }
        if (email.isEmpty())    { etEmail.setError("أدخلي البريد");          return; }
        if (password.length() < 6) { etPassword.setError("6 أحرف على الأقل"); return; }
        if (phone.isEmpty())    { etPhone.setError("أدخلي رقم الهاتف");      return; }
        if (role.equals("doctor") && specialty.isEmpty()) {
            etSpecialty.setError("أدخلي التخصص");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // ✅ إنشاء المستخدم في Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                String uid = authResult.getUser().getUid();

                // ✅ حفظ بيانات المستخدم في Firestore
                Map<String, Object> userData = new HashMap<>();
                userData.put("name",      name);
                userData.put("email",     email);
                userData.put("phone",     phone);
                userData.put("role",      role);      // "patient" أو "doctor"
                userData.put("specialty", specialty); // فارغ للمريض

                db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "تم إنشاء الحساب بنجاح ✅", Toast.LENGTH_SHORT).show();

                        // نوجه للشاشة المناسبة
                        if ("doctor".equals(role)) {
                            startActivity(new Intent(this, DoctorMainActivity.class));
                        } else {
                            startActivity(new Intent(this, PatientMainActivity.class));
                        }
                        finish();
                    });
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(this, "خطأ: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }
}
