# Laporan Refactoring Kode - Quiz Tiga

**Tanggal:** 15 Desember 2025
**Lokasi:** `/home/adam2/Documents/Binus/Sem5/CodeReengineering/AOL/Quiz/tiga`

Berikut adalah detail refactoring yang dilakukan berdasarkan Code Smell yang ditemukan pada package `Quiz/tiga`:

---

### 1. Penggunaan Koleksi Usang (Obsolete Collection)

*   **Nama code smell yang ditemukan:**
    Obsolete Collection / Legacy Implementation (Penggunaan `Vector`).

*   **Alasan terjadinya code smell:**
    Class `Vector` adalah koleksi *thread-safe* tua yang membawa overhead sinkronisasi yang tidak diperlukan dalam konteks aplikasi ini. `ArrayList` adalah standar modern yang lebih cepat untuk list dinamis.

*   **Teknik refactoring yang dilakukan:**
    Replace Vector with List/ArrayList.

*   **Solusi refactoring:**
    Mengganti tipe data `Vector` menjadi `List` (interface) dengan implementasi `ArrayList`.

---

### 2. Angka Ajaib (Magic Number)

*   **Nama code smell yang ditemukan:**
    Magic Number.

*   **Alasan terjadinya code smell:**
    Penggunaan angka literal `100` untuk batas produk tanpa penjelasan kontekstual menyulitkan pemahaman dan pemeliharaan kode.

*   **Teknik refactoring yang dilakukan:**
    Replace Magic Number with Symbolic Constant.

*   **Solusi refactoring:**
    Membuat konstanta `private static final int MAX_PRODUCT_LIMIT = 100` untuk menyimpan nilai batas tersebut.

---

### 3. Enkapsulasi Data (Data Encapsulation)

*   **Nama code smell yang ditemukan:**
    Missing Getter / Incomplete Encapsulation.

*   **Alasan terjadinya code smell:**
    Atribut `stock` memiliki setter tetapi tidak memiliki getter, membuatnya menjadi properti *write-only* yang tidak lazim dan membatasi akses data.

*   **Teknik refactoring yang dilakukan:**
    Encapsulate Field (Add Getter).

*   **Solusi refactoring:**
    Menambahkan method `getStock()` pada class `Product`.

---

### 4. Penanganan Exception (Exception Handling)

*   **Nama code smell yang ditemukan:**
    Throwing Generic Exception.

*   **Alasan terjadinya code smell:**
    Method `addProduct` melempar `Exception` generik, yang merupakan bad practice karena tidak informatif dan menyulitkan *caller* untuk menangani error secara spesifik.

*   **Teknik refactoring yang dilakukan:**
    Replace Generic Exception with Specific Exception (Unchecked).

*   **Solusi refactoring:**
    Menggantinya dengan `IllegalStateException` yang lebih semantik untuk menyatakan bahwa *state* objek (list penuh) tidak mengizinkan operasi penambahan.
