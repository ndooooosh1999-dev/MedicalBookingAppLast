// User.java
// نموذج بيانات المستخدم (مريض أو طبيب)
// مسار الملف: app/src/main/java/com/example/medicalbooking/models/User.java

package com.example.medicalbooking.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String role;      // "patient" أو "doctor"
    private String specialty; // للطبيب فقط

    // ✅ Constructor فارغ - مطلوب لـ Firebase
    public User() {}

    // Constructor كامل
    public User(String uid, String name, String email, String phone, String role, String specialty) {
        this.uid       = uid;
        this.name      = name;
        this.email     = email;
        this.phone     = phone;
        this.role      = role;
        this.specialty = specialty;
    }

    // Getters و Setters
    public String getUid()       { return uid; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getPhone()     { return phone; }
    public String getRole()      { return role; }
    public String getSpecialty() { return specialty; }

    public void setUid(String uid)           { this.uid = uid; }
    public void setName(String name)         { this.name = name; }
    public void setEmail(String email)       { this.email = email; }
    public void setPhone(String phone)       { this.phone = phone; }
    public void setRole(String role)         { this.role = role; }
    public void setSpecialty(String specialty){ this.specialty = specialty; }
}
