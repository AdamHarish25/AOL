# Laporan Refactoring Kode - Quiz Dua

**Tanggal:** 15 Desember 2025  
**Lokasi:** `/home/adam2/Documents/Binus/Sem5/CodeReengineering/AOL/Quiz/dua`

Berikut adalah detail refactoring yang dilakukan berdasarkan Code Smell yang ditemukan:

---

### 1. Masalah Pewarisan (Inheritance)

*   **Nama code smell yang ditemukan:**  
    Refused Bequest (dan Pelanggaran Liskov Substitution Principle).

*   **Alasan terjadinya code smell:**  
    Class `CleaningService` mewarisi class `Employee`, namun secara paksa menonaktifkan method yang diwarisinya (`getKPI` dan `getBonus` yang me-return 0 atau error). Ini terjadi karena parent class `Employee` memiliki tanggung jawab yang terlalu banyak (menyimpan field `kpi`) yang tidak relevan untuk semua jenis subclass-nya.

*   **Teknik refactoring yang dilakukan:**  
    Extract Intermediate Class (Pembuatan Kelas Penengah) dan Push Down Members.

*   **Solusi refactoring:**  
    Kami membuat class abstract baru bernama `PermanentEmployee` yang mewarisi `Employee`. Field `kpi` dan method setter/getter-nya dipindahkan (di-*push down*) dari `Employee` ke `PermanentEmployee`. Class `Manager` dan `Supervisor` kini mewarisi `PermanentEmployee`, sedangkan `CleaningService` tetap mewarisi `Employee` yang sudah dibersihkan.

---

### 2. Duplikasi Kode

*   **Nama code smell yang ditemukan:**  
    Duplicated Code.

*   **Alasan terjadinya code smell:**  
    Terdapat implementasi method `medicalBenefitInfo()` yang sama persis (copy-paste) pada file `Manager.java` dan `Supervisor.java`.

*   **Teknik refactoring yang dilakukan:**  
    Pull Up Method.

*   **Solusi refactoring:**  
    Setelah membuat class `PermanentEmployee`, kami memindahkan (menarik ke atas/pull up) implementasi `medicalBenefitInfo()` ke class `PermanentEmployee`. Dengan demikian, `Manager` dan `Supervisor` otomatis mewarisi method tersebut tanpa perlu menulis ulang kodenya.

---

### 3. Angka-Angka Misterius

*   **Nama code smell yang ditemukan:**  
    Magic Numbers.

*   **Alasan terjadinya code smell:**  
    Penggunaan angka literal (seperti `3.8`, `3.3`, `2.8`, `100`) secara langsung di dalam blok logika (hardcoded). Hal ini membuat kode sulit dipahami maknanya ("apa arti angka 3.8 ini?") dan sulit diubah jika aturan bisnis berubah.

*   **Teknik refactoring yang dilakukan:**  
    Replace Magic Number with Symbolic Constant.

*   **Solusi refactoring:**  
    Mengganti semua angka literal tersebut dengan variabel konstanta (`static final`) yang diberi nama deskriptif.
    *   Contoh: `3.8` diganti menjadi `KPI_GRADE_A_MIN`.
    *   Contoh: `100` diganti menjadi `MAX_EMPLOYEES`.

---

### 4. Struktur Data Usang

*   **Nama code smell yang ditemukan:**  
    Use of Obsolete Collection (Penggunaan struktur data jadul).

*   **Alasan terjadinya code smell:**  
    Class `ListEmployee` menggunakan `java.util.Vector`. `Vector` adalah kelas legacy (peninggalan Java lama) yang bersifat *thread-safe* (synchronized). Penggunaannya dalam aplikasi single-thread seperti ini tidak efisien karena menambah overhead kinerja yang tidak perlu dibandingkan list modern.

*   **Teknik refactoring yang dilakukan:**  
    Encapsulate Collection / Replace Implementation.

*   **Solusi refactoring:**  
    Mengganti tipe data `Vector<Employee>` menjadi `List<Employee>` dengan implementasi `ArrayList<Employee>`. `ArrayList` adalah standar industri saat ini untuk dynamic array yang lebih cepat (unsynchronized) dan umum digunakan.
