// DoctorMainActivity.java
// لوحة تحكم الطبيب - يرى مواعيده ويقبل أو يرفض كل موعد
// مسار الملف: app/src/main/java/com/example/medicalbooking/DoctorMainActivity.java

package com.example.medicalbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalbooking.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class DoctorMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoData, tvWelcome, tvStats;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String doctorId;

    private List<Appointment> appointmentList = new ArrayList<>();
    private AppointmentDoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        // ربط العناصر
        recyclerView = findViewById(R.id.recyclerView);
        progressBar  = findViewById(R.id.progressBar);
        tvNoData     = findViewById(R.id.tvNoData);
        tvWelcome    = findViewById(R.id.tvWelcome);
        tvStats      = findViewById(R.id.tvStats);

        db        = FirebaseFirestore.getInstance();
        mAuth     = FirebaseAuth.getInstance();
        doctorId  = mAuth.getCurrentUser().getUid();

        // إعداد RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppointmentDoctorAdapter();
        recyclerView.setAdapter(adapter);

        // تحميل اسم الطبيب
        loadDoctorName();

        // تحميل المواعيد
        loadAppointments();

        // زر تسجيل الخروج
        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    // تحميل اسم الطبيب
    private void loadDoctorName() {
        db.collection("users").document(doctorId).get()
            .addOnSuccessListener(doc -> {
                String name      = doc.getString("name");
                String specialty = doc.getString("specialty");
                tvWelcome.setText("د. " + name + " 👨‍⚕️");
                if (specialty != null) {
                    tvWelcome.append("\n" + specialty);
                }
            });
    }

    // ✅ تحميل مواعيد الطبيب من Firestore
    private void loadAppointments() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("appointments")
            .whereEqualTo("doctorId", doctorId) // فقط مواعيد هذا الطبيب
            .get()
            .addOnSuccessListener(querySnapshot -> {
                progressBar.setVisibility(View.GONE);
                appointmentList.clear();

                int pending   = 0;
                int confirmed = 0;

                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Appointment appt = doc.toObject(Appointment.class);
                    appt.setAppointmentId(doc.getId());
                    appointmentList.add(appt);

                    // حساب الإحصائيات
                    if ("pending".equals(appt.getStatus()))    pending++;
                    if ("confirmed".equals(appt.getStatus())) confirmed++;
                }

                // عرض الإحصائيات
                tvStats.setText("⏳ قيد الانتظار: " + pending + "   ✅ مؤكدة: " + confirmed);

                if (appointmentList.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            })
            .addOnFailureListener(e ->
                Toast.makeText(this, "خطأ في تحميل المواعيد", Toast.LENGTH_SHORT).show()
            );
    }

    // ✅ قبول الموعد
    private void confirmAppointment(Appointment appt, int position) {
        db.collection("appointments")
            .document(appt.getAppointmentId())
            .update("status", "confirmed")
            .addOnSuccessListener(aVoid -> {
                appointmentList.get(position).setStatus("confirmed");
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "✅ تم قبول الموعد", Toast.LENGTH_SHORT).show();
                // تحديث الإحصائيات
                loadAppointments();
            });
    }

    // ✅ رفض الموعد
    private void cancelAppointment(Appointment appt, int position) {
        db.collection("appointments")
            .document(appt.getAppointmentId())
            .update("status", "cancelled")
            .addOnSuccessListener(aVoid -> {
                appointmentList.get(position).setStatus("cancelled");
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "❌ تم رفض الموعد", Toast.LENGTH_SHORT).show();
                loadAppointments();
            });
    }

    // ✅ Adapter لعرض المواعيد في لوحة الطبيب
    class AppointmentDoctorAdapter extends RecyclerView.Adapter<AppointmentDoctorAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_doctor, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Appointment appt = appointmentList.get(position);

            holder.tvPatientName.setText("المريض: " + appt.getPatientName());
            holder.tvDateTime.setText("📅 " + appt.getDate() + "  ⏰ " + appt.getTime());
            holder.tvStatus.setText(appt.getStatusInArabic());

            // ملاحظات المريض
            if (appt.getNotes() != null && !appt.getNotes().isEmpty()) {
                holder.tvNotes.setText("📝 " + appt.getNotes());
                holder.tvNotes.setVisibility(View.VISIBLE);
            } else {
                holder.tvNotes.setVisibility(View.GONE);
            }

            // تلوين الحالة
            switch (appt.getStatus()) {
                case "confirmed":
                    holder.tvStatus.setTextColor(0xFF2E7D32);
                    holder.btnConfirm.setVisibility(View.GONE);
                    holder.btnCancel.setVisibility(View.GONE);
                    break;
                case "cancelled":
                    holder.tvStatus.setTextColor(0xFFC62828);
                    holder.btnConfirm.setVisibility(View.GONE);
                    holder.btnCancel.setVisibility(View.GONE);
                    break;
                default: // pending
                    holder.tvStatus.setTextColor(0xFFF57F17);
                    // نُظهر زري القبول والرفض فقط للمواعيد المعلقة
                    holder.btnConfirm.setVisibility(View.VISIBLE);
                    holder.btnCancel.setVisibility(View.VISIBLE);
                    holder.btnConfirm.setOnClickListener(v -> confirmAppointment(appt, position));
                    holder.btnCancel.setOnClickListener(v ->  cancelAppointment(appt, position));
            }
        }

        @Override
        public int getItemCount() { return appointmentList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvPatientName, tvDateTime, tvStatus, tvNotes;
            Button btnConfirm, btnCancel;

            ViewHolder(View v) {
                super(v);
                tvPatientName = v.findViewById(R.id.tvPatientName);
                tvDateTime    = v.findViewById(R.id.tvDateTime);
                tvStatus      = v.findViewById(R.id.tvStatus);
                tvNotes       = v.findViewById(R.id.tvNotes);
                btnConfirm    = v.findViewById(R.id.btnConfirm);
                btnCancel     = v.findViewById(R.id.btnCancel);
            }
        }
    }
}
