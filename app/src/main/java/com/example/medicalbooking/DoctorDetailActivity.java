// DoctorDetailActivity.java
// شاشة تفاصيل الطبيب + حجز الموعد
// مسار الملف: app/src/main/java/com/example/medicalbooking/DoctorDetailActivity.java

package com.example.medicalbooking;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medicalbooking.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class DoctorDetailActivity extends AppCompatActivity {

    // عناصر الواجهة
    private TextView tvDoctorName, tvSpecialty, tvPhone;
    private TextView tvSelectedDate, tvSelectedTime;
    private Button btnPickDate, btnPickTime, btnBook;
    private EditText etNotes;
    private ProgressBar progressBar;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // بيانات الطبيب (مُرسَلة من الشاشة السابقة)
    private String doctorId, doctorName, specialty, doctorPhone;

    // بيانات الموعد المختار
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        // ✅ استقبال بيانات الطبيب من PatientMainActivity
        doctorId    = getIntent().getStringExtra("doctorId");
        doctorName  = getIntent().getStringExtra("doctorName");
        specialty   = getIntent().getStringExtra("specialty");
        doctorPhone = getIntent().getStringExtra("phone");

        // ربط العناصر
        tvDoctorName  = findViewById(R.id.tvDoctorName);
        tvSpecialty   = findViewById(R.id.tvSpecialty);
        tvPhone       = findViewById(R.id.tvPhone);
        tvSelectedDate= findViewById(R.id.tvSelectedDate);
        tvSelectedTime= findViewById(R.id.tvSelectedTime);
        btnPickDate   = findViewById(R.id.btnPickDate);
        btnPickTime   = findViewById(R.id.btnPickTime);
        btnBook       = findViewById(R.id.btnBook);
        etNotes       = findViewById(R.id.etNotes);
        progressBar   = findViewById(R.id.progressBar);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // ✅ عرض بيانات الطبيب
        tvDoctorName.setText("د. " + doctorName);
        tvSpecialty.setText(specialty);
        tvPhone.setText("📞 " + doctorPhone);

        // ✅ زر اختيار التاريخ - يفتح DatePicker
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // ✅ زر اختيار الوقت - يفتح TimePicker
        btnPickTime.setOnClickListener(v -> showTimePicker());

        // ✅ زر حجز الموعد
        btnBook.setOnClickListener(v -> bookAppointment());

        // زر الرجوع
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // فتح نافذة اختيار التاريخ
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // تنسيق التاريخ: YYYY-MM-DD
                selectedDate = selectedYear + "-"
                    + String.format("%02d", selectedMonth + 1) + "-"
                    + String.format("%02d", selectedDay);

                tvSelectedDate.setText("📅 " + selectedDate);
            },
            year, month, day
        );

        // لا يسمح باختيار تاريخ في الماضي
        dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        dialog.show();
    }

    // فتح نافذة اختيار الوقت
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour   = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this,
            (view, selectedHour, selectedMinute) -> {
                // تنسيق الوقت: HH:MM
                selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                tvSelectedTime.setText("⏰ " + selectedTime);
            },
            hour, minute, true // true = 24 ساعة
        );
        dialog.show();
    }

    // ✅ حجز الموعد وحفظه في Firestore
    private void bookAppointment() {
        // التحقق من اختيار التاريخ والوقت
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "من فضلك اختاري التاريخ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "من فضلك اختاري الوقت", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnBook.setEnabled(false);

        String patientId = mAuth.getCurrentUser().getUid();
        String notes     = etNotes.getText().toString().trim();

        // ✅ أولاً: نجلب اسم المريض من Firestore
        db.collection("users").document(patientId).get()
            .addOnSuccessListener(document -> {
                String patientName = document.getString("name");

                // ✅ إنشاء كائن الموعد
                Appointment appointment = new Appointment(
                    patientId, patientName,
                    doctorId,  doctorName,
                    specialty, selectedDate, selectedTime, notes
                );

                // ✅ حفظ الموعد في Firestore تحت collection "appointments"
                db.collection("appointments")
                    .add(appointment)
                    .addOnSuccessListener(docRef -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this,
                            "✅ تم حجز موعدك بنجاح!\nبتاريخ: " + selectedDate + " الساعة: " + selectedTime,
                            Toast.LENGTH_LONG).show();

                        finish(); // نرجع للقائمة
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        btnBook.setEnabled(true);
                        Toast.makeText(this, "خطأ في الحجز: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            });
    }
}
