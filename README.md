# 📱 تطبيق حجز المواعيد الطبية
### Medical Appointment Booking App

**اسم الطالبة:** ندى محمد عبد الكريم العرقان
**الجامعة:** جامعة الأقصى - غزة
**التخصص:** برمجة تطبيقات الهواتف الذكية

---

## 🗂️ هيكل الملفات - أين يذهب كل ملف؟

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

## ⚙️ خطوات الإعداد

### 1. إنشاء المشروع في Android Studio
- New Project → Empty Activity
- Name: `MedicalBookingApp`
- Package: `com.example.medicalbooking`
- Language: Java
- Min SDK: API 21

### 2. إعداد Firebase
1. اذهب لـ console.firebase.google.com
2. أنشئ مشروعاً جديداً باسم `MedicalBookingApp`
3. أضف تطبيق Android بالـ Package name أعلاه
4. حمّل `google-services.json` وضعه في مجلد `app/`
5. فعّل: Authentication → Email/Password
6. فعّل: Firestore Database
7. انسخ قواعد الأمان من `firestore.rules`

### 3. تحديث build.gradle
- انسخ محتوى `build.gradle.project.txt` → `build.gradle` (Project)
- انسخ محتوى `build.gradle.app.txt` → `build.gradle` (App)
- اضغط Sync Now

### 4. نسخ الملفات
- انسخ كل ملف Java لمساره الصحيح
- انسخ كل ملف XML لمساره الصحيح
- اضغط Run ▶️

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
