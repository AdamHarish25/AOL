# Laporan Refactoring Kode - Quiz Satu

**Tanggal:** 15 Desember 2025  
**Lokasi:** `/home/adam2/Documents/Binus/Sem5/CodeReengineering/AOL/Quiz/satu`

Berikut adalah detail refactoring yang dilakukan berdasarkan Code Smell yang ditemukan pada package `Quiz/satu`:

---

### 1. Masalah Pewarisan (Inheritance & LSP)

*   **Nama code smell yang ditemukan:**  
    Refused Bequest (Warisan yang Ditolak) & Pelanggaran Liskov Substitution Principle (LSP).

*   **Alasan terjadinya code smell:**  
    Class `BangunRuang` (objek 3D) dipaksa mengimplementasikan interface `BangunDatar` (objek 2D). Akibatnya, `BangunRuang` "menolak" warisan tersebut dengan memberikan implementasi kosong (return 0) pada method `computeArea()` dan `computeAround()`. Ini melanggar prinsip LSP karena `BangunRuang` tidak bisa menggantikan peran `BangunDatar` dengan benar. `Kubus` pun terpaksa memiliki method 2D yang membingungkan.

*   **Teknik refactoring yang dilakukan:**  
    Tease Apart Inheritance (Memisahkan Pewarisan) / Replace Inheritance with Delegation (dalam konteks konseptual, memisahkan hirarki).

*   **Solusi refactoring:**  
    Kami memutuskan hubungan pewarisan yang salah ini.
    1.  Mengubah `BangunRuang` menjadi `abstract class` yang berdiri sendiri (tidak `implements BangunDatar`).
    2.  Menambahkan method abstrak yang relevan untuk 3D: `computeVolume()` dan `computeSurfaceArea()` (menggantikan `computeArea`).
    3.  `Kubus` kini hanya mewarisi `BangunRuang` dan mengimplementasikan method 3D yang tepat.

---

### 2. Enkapsulasi Data (Data Encapsulation)

*   **Nama code smell yang ditemukan:**  
    Deficient Encapsulation / Public Data Members (Atribut Publik).

*   **Alasan terjadinya code smell:**  
    Atribut `sisi` pada class `Persegi` dan `rusuk` pada class `Kubus` didefinisikan sebagai `public`. Hal ini mengekspos detail internal objek dan memungkinkan modifikasi data secara sembarangan dari luar tanpa validasi atau kontrol, yang melanggar prinsip *Information Hiding*.

*   **Teknik refactoring yang dilakukan:**  
    Encapsulate Field.

*   **Solusi refactoring:**  
    1.  Mengubah access modifier atribut (`sisi`, `rusuk`) dari `public` menjadi `private`.
    2.  Membuat method **Getter** dan **Setter** (`getSisi`, `setSisi`, `getRusuk`, `setRusuk`) untuk menyediakan akses terkontrol ke data tersebut.
    3.  Memperbarui `Main.java` untuk menggunakan setter saat inisialisasi objek.
