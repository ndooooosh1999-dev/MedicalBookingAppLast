// DoctorAdapter.java
// هذا الـ Adapter يربط قائمة الأطباء بـ RecyclerView
// مسار الملف: app/src/main/java/com/example/medicalbooking/DoctorAdapter.java

package com.example.medicalbooking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medicalbooking.models.User;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private List<User> doctorList;

    // Interface للتعامل مع الضغط على طبيب
    public interface OnDoctorClickListener {
        void onDoctorClick(User doctor);
    }

    private OnDoctorClickListener listener;

    // Constructor
    public DoctorAdapter(Context context, List<User> doctorList, OnDoctorClickListener listener) {
        this.context    = context;
        this.doctorList = doctorList;
        this.listener   = listener;
    }

    // ✅ هنا نحدد شكل كل عنصر في القائمة
    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    // ✅ هنا نملأ كل عنصر ببيانات الطبيب
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        User doctor = doctorList.get(position);

        holder.tvDoctorName.setText("د. " + doctor.getName());
        holder.tvSpecialty.setText(doctor.getSpecialty());
        holder.tvPhone.setText("📞 " + doctor.getPhone());

        // عند الضغط على الطبيب
        holder.itemView.setOnClickListener(v -> listener.onDoctorClick(doctor));
    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    // ✅ ViewHolder - يحمل مراجع عناصر الواجهة لكل عنصر
    static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvSpecialty, tvPhone;

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty  = itemView.findViewById(R.id.tvSpecialty);
            tvPhone      = itemView.findViewById(R.id.tvPhone);
        }
    }
}
