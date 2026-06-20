// Appointment.java
// نموذج بيانات الموعد الطبي
// مسار الملف: app/src/main/java/com/example/medicalbooking/models/Appointment.java

package com.example.medicalbooking.models;

public class Appointment {
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String specialty;
    private String date;       // مثال: "2025-03-15"
    private String time;       // مثال: "10:00 AM"
    private String status;     // "pending" أو "confirmed" أو "cancelled"
    private String notes;      // ملاحظات المريض

    // ✅ Constructor فارغ - مطلوب لـ Firebase
    public Appointment() {}

    // Constructor كامل
    public Appointment(String patientId, String patientName,
                       String doctorId, String doctorName,
                       String specialty, String date, String time, String notes) {
        this.patientId   = patientId;
        this.patientName = patientName;
        this.doctorId    = doctorId;
        this.doctorName  = doctorName;
        this.specialty   = specialty;
        this.date        = date;
        this.time        = time;
        this.notes       = notes;
        this.status      = "pending"; // الحالة الابتدائية: قيد الانتظار
    }

    // Getters
    public String getAppointmentId() { return appointmentId; }
    public String getPatientId()     { return patientId; }
    public String getPatientName()   { return patientName; }
    public String getDoctorId()      { return doctorId; }
    public String getDoctorName()    { return doctorName; }
    public String getSpecialty()     { return specialty; }
    public String getDate()          { return date; }
    public String getTime()          { return time; }
    public String getStatus()        { return status; }
    public String getNotes()         { return notes; }

    // Setters
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    public void setPatientId(String patientId)         { this.patientId = patientId; }
    public void setPatientName(String patientName)     { this.patientName = patientName; }
    public void setDoctorId(String doctorId)           { this.doctorId = doctorId; }
    public void setDoctorName(String doctorName)       { this.doctorName = doctorName; }
    public void setSpecialty(String specialty)         { this.specialty = specialty; }
    public void setDate(String date)                   { this.date = date; }
    public void setTime(String time)                   { this.time = time; }
    public void setStatus(String status)               { this.status = status; }
    public void setNotes(String notes)                 { this.notes = notes; }

    // دالة مساعدة - ترجمة الحالة للعربية
    public String getStatusInArabic() {
        switch (status) {
            case "confirmed":  return "✅ مؤكد";
            case "cancelled":  return "❌ ملغى";
            default:           return "⏳ قيد الانتظار";
        }
    }
}
