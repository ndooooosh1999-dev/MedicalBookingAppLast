# 📱 تطبيق حجز المواعيد الطبية
### Medical Appointment Booking App

**اسم الطالبة:** ندى محمد عبد الكريم العرقان
**الجامعة:** جامعة الأقصى - غزة
**التخصص:** برمجة تطبيقات الهواتف الذكية

---
هيكل الملفات 

```
MedicalBookingApp/
│
├── build.gradle  ← (Project level) - المجلد الرئيسي للمشروع
│
└── app/
    ├── build.gradle  ← (App level) - داخل مجلد app/
    │
    └── src/main/
        │
        ├── AndroidManifest.xml  ← هنا مباشرة
        │
        ├── java/com/example/medicalbooking/
        │   │
        │   ├── SplashActivity.java       ✅ شاشة البداية
        │   ├── LoginActivity.java        ✅ تسجيل الدخول
        │   ├── RegisterActivity.java     ✅ إنشاء حساب
        │   ├── PatientMainActivity.java  ✅ شاشة المريض
        │   ├── DoctorDetailActivity.java ✅ تفاصيل الطبيب + حجز
        │   ├── MyAppointmentsActivity.java ✅ مواعيدي
        │   ├── DoctorMainActivity.java   ✅ لوحة تحكم الطبيب
        │   ├── DoctorAdapter.java        ✅ قائمة الأطباء
        │   │
        │   └── models/
        │       ├── User.java             ✅ نموذج المستخدم
        │       └── Appointment.java      ✅ نموذج الموعد
        │
        └── res/layout/
            ├── activity_splash.xml           ✅
            ├── activity_login.xml            ✅
            ├── activity_register.xml         ✅
            ├── activity_patient_main.xml     ✅
            ├── activity_doctor_detail.xml    ✅
            ├── activity_my_appointments.xml  ✅
            ├── activity_doctor_main.xml      ✅
            ├── item_doctor.xml               ✅
            ├── item_appointment.xml          ✅
            └── item_appointment_doctor.xml   ✅
```

---

## 🔧 التقنيات المستخدمة
| التقنية | الاستخدام |
|---|---|
| Java | لغة البرمجة |
| Android Studio | بيئة التطوير |
| Firebase Auth | تسجيل الدخول |
| Firebase Firestore | قاعدة البيانات السحابية |
| RecyclerView | عرض القوائم |
| Material Design | تصميم الواجهات |
| MVVM Pattern | معمارية التطبيق |

---

## 👥 أنواع المستخدمين

### 🙋 المريض
- تسجيل حساب جديد
- تصفح قائمة الأطباء
- حجز موعد (تاريخ + وقت + ملاحظات)
- عرض مواعيده وحالتها
- إلغاء موعد قيد الانتظار

### 👨‍⚕️ الطبيب
- تسجيل حساب بتخصصه الطبي
- عرض طلبات المواعيد
- قبول أو رفض كل موعد
- رؤية إحصائيات المواعيد

---

## 🗄️ هيكل قاعدة البيانات (Firestore)

### Collection: `users`
```
users/
  {userId}/
    name: "ندى محمد"
    email: "nada@example.com"
    phone: "0599123456"
    role: "patient" أو "doctor"
    specialty: "طب عام" (للطبيب فقط)
```

### Collection: `appointments`
```
appointments/
  {appointmentId}/
    patientId: "uid..."
    patientName: "ندى محمد"
    doctorId: "uid..."
    doctorName: "محمد أحمد"
    specialty: "طب عام"
    date: "2025-03-15"
    time: "10:00"
    status: "pending" | "confirmed" | "cancelled"
    notes: "ألم في المعدة"
```
