// SplashActivity.java
// شاشة البداية - تظهر ثانيتين ثم تتحقق هل المستخدم مسجل دخوله أم لا

package com.example.medicalbooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // ✅ بعد ثانيتين (2000 مللي ثانية) نتحقق من حالة تسجيل الدخول
        new Handler().postDelayed(() -> {

            // نجلب المستخدم الحالي من Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                // ✅ المستخدم مسجل دخوله من قبل - نروح للشاشة الرئيسية
                // ملاحظة: سنحدد نوع المستخدم (طبيب/مريض) لاحقاً من Firestore
                goToMain(currentUser.getUid());
            } else {
                // ❌ لا يوجد مستخدم - نروح لشاشة تسجيل الدخول
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // نغلق شاشة Splash حتى لا يرجع لها المستخدم بزر الرجوع

        }, 2000); // 2 ثانية
    }

    // دالة تحدد إلى أي شاشة رئيسية يذهب المستخدم بناءً على نوعه
    private void goToMain(String uid) {
        // نجلب نوع المستخدم من Firestore
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");

                        if ("doctor".equals(role)) {
                            // طبيب → شاشة الطبيب
                            startActivity(new Intent(this, DoctorMainActivity.class));
                        } else {
                            // مريض → شاشة المريض
                            startActivity(new Intent(this, PatientMainActivity.class));
                        }
                    } else {
                        // لا يوجد بيانات - نرجع لتسجيل الدخول
                        startActivity(new Intent(this, LoginActivity.class));
                    }
                });
    }
}
