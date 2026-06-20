// PatientMainActivity.java
// الشاشة الرئيسية للمريض - تعرض قائمة الأطباء
// مسار الملف: app/src/main/java/com/example/medicalbooking/PatientMainActivity.java

package com.example.medicalbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalbooking.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PatientMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoData;
    private TextView tvWelcome;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<User> doctorList = new ArrayList<>();
    private DoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        // ربط العناصر
        recyclerView = findViewById(R.id.recyclerView);
        progressBar  = findViewById(R.id.progressBar);
        tvNoData     = findViewById(R.id.tvNoData);
        tvWelcome    = findViewById(R.id.tvWelcome);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // ✅ إعداد RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ إعداد الـ Adapter مع الضغط على الطبيب
        adapter = new DoctorAdapter(this, doctorList, doctor -> {
            // عند الضغط على طبيب → نروح لشاشة تفاصيله
            Intent intent = new Intent(PatientMainActivity.this, DoctorDetailActivity.class);
            intent.putExtra("doctorId",   doctor.getUid());
            intent.putExtra("doctorName", doctor.getName());
            intent.putExtra("specialty",  doctor.getSpecialty());
            intent.putExtra("phone",      doctor.getPhone());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // ✅ تحميل اسم المريض
        loadPatientName();

        // ✅ تحميل قائمة الأطباء
        loadDoctors();

        // زر "مواعيدي"
        findViewById(R.id.btnMyAppointments).setOnClickListener(v ->
            startActivity(new Intent(this, MyAppointmentsActivity.class))
        );

        // زر "تسجيل الخروج"
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // تحميل اسم المريض وعرضه في الترحيب
    private void loadPatientName() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
            .addOnSuccessListener(document -> {
                String name = document.getString("name");
                tvWelcome.setText("أهلاً، " + name + " 👋");
            });
    }

    // ✅ تحميل قائمة الأطباء من Firestore
    private void loadDoctors() {
        progressBar.setVisibility(View.VISIBLE);

        // نجلب كل المستخدمين الذين role = "doctor"
        db.collection("users")
            .whereEqualTo("role", "doctor")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                progressBar.setVisibility(View.GONE);
                doctorList.clear();

                for (QueryDocumentSnapshot doc : querySnapshot) {
                    User doctor = doc.toObject(User.class);
                    doctor.setUid(doc.getId()); // نحفظ الـ ID
                    doctorList.add(doctor);
                }

                if (doctorList.isEmpty()) {
                    // لا يوجد أطباء بعد
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged(); // تحديث القائمة
                }
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "خطأ في تحميل البيانات", Toast.LENGTH_SHORT).show();
            });
    }
}
