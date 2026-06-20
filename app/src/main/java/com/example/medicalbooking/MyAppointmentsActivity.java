// MyAppointmentsActivity.java
// شاشة "مواعيدي" للمريض - تعرض كل مواعيده مع حالتها
// مسار الملف: app/src/main/java/com/example/medicalbooking/MyAppointmentsActivity.java

package com.example.medicalbooking;

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

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private FirebaseFirestore db;
    private String patientId;

    private List<Appointment> appointmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar  = findViewById(R.id.progressBar);
        tvNoData     = findViewById(R.id.tvNoData);

        db        = FirebaseFirestore.getInstance();
        patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // زر الرجوع
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadMyAppointments();
    }

    // ✅ تحميل مواعيد المريض من Firestore
    private void loadMyAppointments() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("appointments")
            .whereEqualTo("patientId", patientId) // فقط مواعيد هذا المريض
            .get()
            .addOnSuccessListener(querySnapshot -> {
                progressBar.setVisibility(View.GONE);
                appointmentList.clear();

                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Appointment appt = doc.toObject(Appointment.class);
                    appt.setAppointmentId(doc.getId());
                    appointmentList.add(appt);
                }

                if (appointmentList.isEmpty()) {
                    tvNoData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new AppointmentAdapter());
                }
            })
            .addOnFailureListener(e ->
                Toast.makeText(this, "خطأ في تحميل المواعيد", Toast.LENGTH_SHORT).show()
            );
    }

    // ✅ Adapter داخلي لعرض المواعيد
    class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Appointment appt = appointmentList.get(position);

            holder.tvDoctorName.setText("د. " + appt.getDoctorName());
            holder.tvSpecialty.setText(appt.getSpecialty());
            holder.tvDate.setText("📅 " + appt.getDate() + "  ⏰ " + appt.getTime());
            holder.tvStatus.setText(appt.getStatusInArabic());

            // تلوين الحالة
            switch (appt.getStatus()) {
                case "confirmed":
                    holder.tvStatus.setTextColor(0xFF2E7D32); // أخضر
                    break;
                case "cancelled":
                    holder.tvStatus.setTextColor(0xFFC62828); // أحمر
                    break;
                default:
                    holder.tvStatus.setTextColor(0xFFF57F17); // برتقالي
            }

            // ✅ زر إلغاء الموعد (فقط إذا كان قيد الانتظار)
            if ("pending".equals(appt.getStatus())) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(v -> cancelAppointment(appt, position));
            } else {
                holder.btnCancel.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return appointmentList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDoctorName, tvSpecialty, tvDate, tvStatus;
            Button btnCancel;

            ViewHolder(View v) {
                super(v);
                tvDoctorName = v.findViewById(R.id.tvDoctorName);
                tvSpecialty  = v.findViewById(R.id.tvSpecialty);
                tvDate       = v.findViewById(R.id.tvDate);
                tvStatus     = v.findViewById(R.id.tvStatus);
                btnCancel    = v.findViewById(R.id.btnCancel);
            }
        }
    }

    // ✅ إلغاء الموعد - تغيير الحالة إلى "cancelled" في Firestore
    private void cancelAppointment(Appointment appt, int position) {
        db.collection("appointments")
            .document(appt.getAppointmentId())
            .update("status", "cancelled")
            .addOnSuccessListener(aVoid -> {
                appointmentList.get(position).setStatus("cancelled");
                recyclerView.getAdapter().notifyItemChanged(position);
                Toast.makeText(this, "تم إلغاء الموعد", Toast.LENGTH_SHORT).show();
            });
    }
}
