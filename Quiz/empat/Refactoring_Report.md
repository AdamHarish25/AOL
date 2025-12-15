
# Laporan Refactoring Kode - Quiz Empat

**Tanggal:** 15 Desember 2025
**Lokasi:** `/home/adam2/Documents/Binus/Sem5/CodeReengineering/AOL/Quiz/empat`

Berikut adalah detail refactoring yang dilakukan berdasarkan Code Smell yang ditemukan pada package `Quiz/empat`:

---

### 1. Obsolete Collection

*   **Nama code smell yang ditemukan:**
    Obsolete Collection (Penggunaan `Vector`).

*   **Alasan terjadinya code smell:**
    Class `Vector` adalah koleksi *legacy* yang *thread-safe* (synchronized). Dalam konteks aplikasi *single-threaded* atau di mana sinkronisasi tidak diperlukan, penggunaan `Vector` memberikan *overhead* performa yang sia-sia dibandingkan dengan `ArrayList`.

*   **Teknik refactoring yang dilakukan:**
    Replace Vector with List/ArrayList.

*   **Solusi refactoring:**
    Mengganti deklarasi `Vector<Item> items` menjadi `List<Item> items = new ArrayList<>();` dan mengimpor `java.util.List` serta `java.util.ArrayList`.

---

### 2. Data Clumps

*   **Nama code smell yang ditemukan:**
    Data Clumps.

*   **Alasan terjadinya code smell:**
    Atribut `lonOrigin`, `latOrigin` serta `lonDestination`, `latDestination` pada class `Order` selalu muncul bersamaan dan memiliki kohesi yang kuat sebagai representasi koordinat geografis. Membiarkan mereka terpisah mengaburkan struktur data dan menyulitkan *passing* parameter.

*   **Teknik refactoring yang dilakukan:**
    Extract Class.

*   **Solusi refactoring:**
    Membuat class baru bernama `Coordinate` yang memiliki atribut `latitude` dan `longitude`. Class `Order` kemudian dimodifikasi untuk menggunakan objek `Coordinate` (`origin` dan `destination`) alih-alih variabel primitif terpisah. Setter dan getter disesuaikan untuk mendelegasikan akses ke objek `Coordinate` tersebut (Preserve Whole Object / Encapsulation).

---

### 3. Magic Numbers & Literals

*   **Nama code smell yang ditemukan:**
    Magic Numbers & String Literals.

*   **Alasan terjadinya code smell:**
    Penggunaan angka literal seperti `6371` dan string `"JNE"`, `"JNT"`, `"tiki"` secara langsung di dalam logika kode membuat kode sulit dimengerti (*readability* buruk) dan rentan terhadap kesalahan ketik (*typo*) saat pemeliharaan.

*   **Teknik refactoring yang dilakukan:**
    Replace Magic Literal with Constants.

*   **Solusi refactoring:**
    Mendefinisikan konstanta `static final` pada class `Shipping`:
    - `EARTH_RADIUS_KM = 6371`
    - `SERVICE_JNE = "JNE"`
    - `SERVICE_JNT = "JNT"`
    - `SERVICE_TIKI = "tiki"`
    Lalu mengganti semua kemunculan literal tersebut dengan konstanta yang sesuai.

---

### 4. Switch Statements (Complex Conditionals)

*   **Nama code smell yang ditemukan:**
    Switch Statements / Chain of `if-else`.

*   **Alasan terjadinya code smell:**
    Logika percabangan yang berulang pada method `getEstimationDays` dan `getShippingPrice` berdasarkan tipe pengiriman melanggar *Open/Closed Principle*. Jika ada penambahan layanan pengiriman baru, class `Shipping` harus dimodifikasi di banyak tempat.

*   **Teknik refactoring yang dilakukan:**
    Replace Conditional with Polymorphism (Analisis/Rekomendasi).

*   **Solusi refactoring:**
    *Catatan: Refactoring ini direkomendasikan untuk langkah selanjutnya.*
    Solusinya adalah mengubah `Shipping` menjadi *abstract class* atau *interface* dengan method `getEstimationDays()` dan `getShippingPrice(Order order)`. Kemudian membuat class turunan untuk setiap layanan (misal: `JNEShipping`, `JNTShipping`) yang mengimplementasikan logika spesifik masing-masing. Logic pembuatan objek `Shipping` dapat dipindahkan ke *Factory Pattern*. Saat ini, kode telah disederhanakan dengan penggunaan konstanta untuk mempermudah transisi ke pola ini nantinya.
